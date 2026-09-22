package com.careflow.app.feature.documents

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.careflow.app.domain.model.CareTask
import com.careflow.app.domain.model.DocumentProcessingState
import com.careflow.app.domain.model.DocumentType
import com.careflow.app.domain.model.HealthDocument
import com.careflow.app.domain.model.TaskType
import com.careflow.app.domain.repository.CareRepository
import com.careflow.app.feature.documents.extraction.ExtractionResult
import com.careflow.app.feature.documents.extraction.RuleBasedMedicalInfoExtractor
import com.careflow.app.feature.documents.ocr.DocumentTextExtractor
import com.careflow.app.feature.documents.ocr.MlKitDocumentTextExtractor
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// OCR state
// ─────────────────────────────────────────────────────────────────────────────

sealed class OcrState {
    object Idle : OcrState()
    object Processing : OcrState()
    data class TextExtracted(val text: String, val documentId: String) : OcrState()
    data class Failed(val documentId: String) : OcrState()
    data class ExtractionReview(
        val documentId: String,
        val confirmedText: String,
        val extractionResult: ExtractionResult
    ) : OcrState()
    object Confirmed : OcrState()
}

// ─────────────────────────────────────────────────────────────────────────────
// UI state
// ─────────────────────────────────────────────────────────────────────────────

data class DocumentCaptureUiState(
    val capturedImageUri: Uri? = null,
    // Stable absolute filesystem path used for DB persistence; distinct from the
    // transient FileProvider content:// URI that is only valid for the camera intent.
    val capturedImageFilePath: String? = null,
    val cameraImageUri: Uri? = null,
    val cameraImageFilePath: String? = null,
    val shouldRequestCameraPermission: Boolean = false,
    val shouldLaunchCamera: Boolean = false,
    val errorMessage: String? = null,
    val ocrState: OcrState = OcrState.Idle,
    val isEditingText: Boolean = false,
    val editableText: String = ""
)

// ─────────────────────────────────────────────────────────────────────────────
// ViewModel
// ─────────────────────────────────────────────────────────────────────────────

private const val OCR_TIMEOUT_MS = 30_000L

class DocumentCaptureViewModel(
    private val repository: CareRepository,
    // Must be applicationContext — never store an Activity context in a ViewModel.
    private val appContext: Context,
    private val textExtractor: DocumentTextExtractor = MlKitDocumentTextExtractor(),
    private val medicalInfoExtractor: com.careflow.app.feature.documents.extraction.MedicalInfoExtractor = RuleBasedMedicalInfoExtractor()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentCaptureUiState())
    val uiState: StateFlow<DocumentCaptureUiState> = _uiState.asStateFlow()

    // ── Camera permission ─────────────────────────────────────────────────────

    fun requestCameraPermission() {
        _uiState.update { it.copy(shouldRequestCameraPermission = true) }
    }

    fun onCameraPermissionGranted() {
        val photoFile = createImageFile()
        val photoUri = FileProvider.getUriForFile(
            appContext,
            "${appContext.packageName}.fileprovider",
            photoFile
        )
        _uiState.update {
            it.copy(
                cameraImageUri = photoUri,
                cameraImageFilePath = photoFile.absolutePath,
                shouldLaunchCamera = true,
                shouldRequestCameraPermission = false,
                errorMessage = null
            )
        }
    }

    fun onCameraPermissionDenied() {
        _uiState.update {
            it.copy(
                shouldRequestCameraPermission = false,
                errorMessage = "Camera permission is required to take photos"
            )
        }
    }

    fun onCameraLaunched() {
        _uiState.update { it.copy(shouldLaunchCamera = false) }
    }

    fun onPhotoTaken() {
        _uiState.update {
            it.copy(
                capturedImageUri = it.cameraImageUri,
                capturedImageFilePath = it.cameraImageFilePath,
                cameraImageUri = null,
                cameraImageFilePath = null
            )
        }
    }

    // ── Gallery selection ─────────────────────────────────────────────────────

    fun onImageSelected(uri: Uri) {
        try {
            val (copiedUri, filePath) = copyImageToAppStorage(uri)
            _uiState.update {
                it.copy(
                    capturedImageUri = copiedUri,
                    capturedImageFilePath = filePath,
                    errorMessage = null
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(errorMessage = "Could not load the selected image. Please try another.")
            }
        }
    }

    // ── Reset ─────────────────────────────────────────────────────────────────

    fun resetCapture() {
        _uiState.update {
            it.copy(
                capturedImageUri = null,
                capturedImageFilePath = null,
                cameraImageUri = null,
                cameraImageFilePath = null,
                errorMessage = null,
                ocrState = OcrState.Idle
            )
        }
    }

    fun resetToOptions() {
        _uiState.update { DocumentCaptureUiState() }
    }

    // ── Document save + OCR ───────────────────────────────────────────────────

    fun saveDocumentAndExtractText() {
        val imageUri = _uiState.value.capturedImageUri ?: return
        // Use the stable absolute path for DB storage. Fall back to the URI string
        // only if the path was not captured (should not happen in normal flow).
        val imageFilePath = _uiState.value.capturedImageFilePath ?: imageUri.toString()

        viewModelScope.launch {
            val documentId = UUID.randomUUID().toString()
            val document = HealthDocument(
                id = documentId,
                careEpisodeId = "episode_1",
                documentType = DocumentType.OTHER,
                title = "Health Document",
                filePath = imageFilePath,
                capturedAt = LocalDateTime.now(),
                processingState = DocumentProcessingState.PROCESSING
            )

            try {
                repository.addHealthDocument(document)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to save document") }
                return@launch
            }

            // Update UI to PROCESSING immediately so the spinner appears before OCR starts.
            _uiState.update { it.copy(ocrState = OcrState.Processing) }

            // Run OCR on a background thread (handled inside MlKitDocumentTextExtractor).
            // Apply a hard timeout: if ML Kit does not respond within OCR_TIMEOUT_MS the
            // document is marked FAILED. Note that TimeoutCancellationException only
            // cancels the withTimeout child scope — the outer viewModelScope is unaffected.
            val result: Result<String> = try {
                withTimeout(OCR_TIMEOUT_MS) {
                    textExtractor.extractText(imageUri, appContext)
                }
            } catch (e: TimeoutCancellationException) {
                Result.failure(Exception("OCR timed out"))
            }

            result.fold(
                onSuccess = { text ->
                    try {
                        repository.updateDocumentProcessing(
                            documentId = documentId,
                            state = DocumentProcessingState.TEXT_EXTRACTED,
                            extractedText = text,
                            error = null
                        )
                    } catch (_: Exception) { /* Non-fatal: proceed to review regardless. */ }

                    _uiState.update {
                        it.copy(
                            ocrState = OcrState.TextExtracted(text = text, documentId = documentId)
                        )
                    }
                },
                onFailure = {
                    try {
                        repository.updateDocumentProcessing(
                            documentId = documentId,
                            state = DocumentProcessingState.FAILED,
                            extractedText = null,
                            error = null
                        )
                    } catch (_: Exception) {}

                    _uiState.update {
                        it.copy(ocrState = OcrState.Failed(documentId = documentId))
                    }
                }
            )
        }
    }

    // ── Manual entry ──────────────────────────────────────────────────────────

    fun handleManualEntry() {
        // Insert the document into Room first so that `confirmExtractedText` has a
        // real row to UPDATE. Previously, the document was never inserted here, causing
        // the confirmation UPDATE to silently affect 0 rows while the UI showed "saved".
        viewModelScope.launch {
            val documentId = UUID.randomUUID().toString()
            val document = HealthDocument(
                id = documentId,
                careEpisodeId = "episode_1",
                documentType = DocumentType.OTHER,
                title = "Manual entry",
                filePath = "",
                capturedAt = LocalDateTime.now(),
                processingState = DocumentProcessingState.CAPTURED
            )
            try {
                repository.addHealthDocument(document)
                _uiState.update {
                    it.copy(
                        isEditingText = true,
                        editableText = "",
                        ocrState = OcrState.TextExtracted(text = "", documentId = documentId)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Could not create document. Please try again.")
                }
            }
        }
    }

    // ── Text review / editing ─────────────────────────────────────────────────

    fun confirmExtractedText(documentId: String, confirmedText: String) {
        viewModelScope.launch {
            try {
                repository.updateDocumentProcessing(
                    documentId = documentId,
                    state = DocumentProcessingState.CONFIRMED,
                    extractedText = confirmedText,
                    error = null
                )
            } catch (_: Exception) {}

            // Run rule-based extraction on the confirmed text. If medications are found,
            // show a review dialog so the user can approve before tasks are created.
            val extraction = medicalInfoExtractor.extract(confirmedText)
            if (extraction.medications.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        ocrState = OcrState.ExtractionReview(
                            documentId = documentId,
                            confirmedText = confirmedText,
                            extractionResult = extraction
                        )
                    )
                }
            } else {
                _uiState.update { it.copy(ocrState = OcrState.Confirmed) }
            }
        }
    }

    fun confirmExtraction(tasks: List<CareTask>) {
        viewModelScope.launch {
            try {
                repository.addCareTasks(tasks)
            } catch (_: Exception) {}
            _uiState.update { it.copy(ocrState = OcrState.Confirmed) }
        }
    }

    fun dismissExtractionReview() {
        _uiState.update { it.copy(ocrState = OcrState.Confirmed) }
    }

    fun startEditingText(currentText: String) {
        _uiState.update {
            it.copy(isEditingText = true, editableText = currentText)
        }
    }

    fun onEditableTextChanged(text: String) {
        _uiState.update { it.copy(editableText = text) }
    }

    fun cancelEditing() {
        _uiState.update { it.copy(isEditingText = false, editableText = "") }
    }

    fun saveEditedText(documentId: String) {
        val correctedText = _uiState.value.editableText
        _uiState.update {
            it.copy(
                isEditingText = false,
                editableText = "",
                ocrState = OcrState.TextExtracted(text = correctedText, documentId = documentId)
            )
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun createImageFile(): File {
        // getExternalFilesDir returns null when external storage is unavailable.
        // Fall back to internal storage so capture never silently fails.
        val storageDir = appContext.getExternalFilesDir(null) ?: appContext.filesDir
        val timeStamp = System.currentTimeMillis()
        return File.createTempFile("CAREFLOW_$timeStamp", ".jpg", storageDir)
    }

    private fun copyImageToAppStorage(sourceUri: Uri): Pair<Uri, String> {
        val fileName = "document_${System.currentTimeMillis()}.jpg"
        val destinationFile = File(appContext.filesDir, fileName)

        val inputStream = appContext.contentResolver.openInputStream(sourceUri)
            ?: throw IOException("Image source is not accessible")

        inputStream.use { input ->
            destinationFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        if (destinationFile.length() == 0L) {
            destinationFile.delete()
            throw IOException("Image appears to be empty or unreadable")
        }

        return Pair(Uri.fromFile(destinationFile), destinationFile.absolutePath)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Factory — allows lifecycle-aware ViewModelProvider to construct this ViewModel
// without a DI framework. Requires applicationContext (not Activity context).
// ─────────────────────────────────────────────────────────────────────────────

class DocumentCaptureViewModelFactory(
    private val repository: CareRepository,
    private val appContext: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        DocumentCaptureViewModel(repository, appContext) as T
}
