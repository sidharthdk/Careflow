package com.careflow.app.feature.documents

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.careflow.app.CareFlowApplication
import com.careflow.app.core.ui.theme.TextSecondary

// ─────────────────────────────────────────────────────────────────────────────
// Entry-point composable — drives the whole capture + OCR flow via state.
// The ViewModel is lifecycle-aware (viewModel()) so its state survives rotation.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DocumentCaptureDialog(
    onDismiss: () -> Unit,
    onDocumentCaptured: (Uri) -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as CareFlowApplication

    val viewModel: DocumentCaptureViewModel = viewModel(
        factory = DocumentCaptureViewModelFactory(application.repository, application)
    )
    val uiState by viewModel.uiState.collectAsState()

    // Wraps every explicit dismiss path so that ViewModel state is always cleared
    // when the user intentionally closes the dialog. This ensures the next open
    // starts at the options screen rather than in a stale intermediate state.
    val dismissAndReset: () -> Unit = {
        viewModel.resetToOptions()
        onDismiss()
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) viewModel.onCameraPermissionGranted()
        else viewModel.onCameraPermissionDenied()
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { viewModel.onImageSelected(it) } }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success -> if (success) viewModel.onPhotoTaken() }

    LaunchedEffect(uiState.shouldRequestCameraPermission) {
        if (!uiState.shouldRequestCameraPermission) return@LaunchedEffect
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            viewModel.onCameraPermissionGranted()
        } else {
            cameraPermissionLauncher.launch(permission)
        }
    }

    LaunchedEffect(uiState.shouldLaunchCamera, uiState.cameraImageUri) {
        if (uiState.shouldLaunchCamera && uiState.cameraImageUri != null) {
            cameraLauncher.launch(uiState.cameraImageUri)
            viewModel.onCameraLaunched()
        }
    }

    when {
        // Text editor — sits on top of the review state
        uiState.isEditingText -> {
            val docId = (uiState.ocrState as? OcrState.TextExtracted)?.documentId ?: ""
            DocumentTextEditDialog(
                initialText = uiState.editableText,
                onTextChanged = { viewModel.onEditableTextChanged(it) },
                onCancel = { viewModel.cancelEditing() },
                onSave = { viewModel.saveEditedText(docId) }
            )
        }

        uiState.ocrState is OcrState.Confirmed -> {
            DocumentSavedDialog(
                onDismiss = {
                    val capturedUri = uiState.capturedImageUri
                    viewModel.resetToOptions()
                    if (capturedUri != null) onDocumentCaptured(capturedUri)
                    onDismiss()
                }
            )
        }

        uiState.ocrState is OcrState.ExtractionReview -> {
            val state = uiState.ocrState as OcrState.ExtractionReview
            ExtractionReviewDialog(
                extractionResult = state.extractionResult,
                onConfirm = { tasks -> viewModel.confirmExtraction(tasks) },
                onSkip = { viewModel.dismissExtractionReview() }
            )
        }

        uiState.ocrState is OcrState.TextExtracted -> {
            val state = uiState.ocrState as OcrState.TextExtracted
            DocumentReviewDialog(
                extractedText = state.text,
                onConfirm = { text -> viewModel.confirmExtractedText(state.documentId, text) },
                onEditText = { viewModel.startEditingText(state.text) },
                onTryAgain = { viewModel.resetCapture() },
                onDismiss = dismissAndReset
            )
        }

        uiState.ocrState is OcrState.Failed -> {
            OcrFailedDialog(
                onRetakePhoto = {
                    viewModel.resetCapture()
                    viewModel.requestCameraPermission()
                },
                onChooseImage = {
                    viewModel.resetCapture()
                    galleryLauncher.launch("image/*")
                },
                onEnterManually = { viewModel.handleManualEntry() },
                onDismiss = dismissAndReset
            )
        }

        uiState.ocrState is OcrState.Processing -> {
            OcrProcessingDialog()
        }

        uiState.capturedImageUri != null -> {
            DocumentPreviewDialog(
                imageUri = uiState.capturedImageUri!!,
                onUseDocument = { viewModel.saveDocumentAndExtractText() },
                onRetake = { viewModel.resetCapture() },
                onDismiss = dismissAndReset
            )
        }

        else -> {
            AddDocumentOptionsDialog(
                onTakePhoto = { viewModel.requestCameraPermission() },
                onChooseFromGallery = { galleryLauncher.launch("image/*") },
                onEnterManually = { viewModel.handleManualEntry() },
                onDismiss = dismissAndReset,
                errorMessage = uiState.errorMessage
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 1 — choose capture method
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AddDocumentOptionsDialog(
    onTakePhoto: () -> Unit,
    onChooseFromGallery: () -> Unit,
    onEnterManually: () -> Unit,
    onDismiss: () -> Unit,
    errorMessage: String?
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {

                Text(
                    text = "Add health document",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                errorMessage?.let { msg ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                DocumentOptionRow(
                    icon = Icons.Default.CameraAlt,
                    title = "Take a photo",
                    subtitle = "Capture with camera",
                    onClick = onTakePhoto
                )
                Spacer(modifier = Modifier.height(8.dp))
                DocumentOptionRow(
                    icon = Icons.Default.PhotoLibrary,
                    title = "Choose from gallery",
                    subtitle = "Select existing photo",
                    onClick = onChooseFromGallery
                )
                Spacer(modifier = Modifier.height(8.dp))
                DocumentOptionRow(
                    icon = Icons.Default.Edit,
                    title = "Enter manually",
                    subtitle = "Type information",
                    onClick = onEnterManually
                )

                Spacer(modifier = Modifier.height(20.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
private fun DocumentOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 2 — preview captured image
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DocumentPreviewDialog(
    imageUri: Uri,
    onUseDocument: () -> Unit,
    onRetake: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Document captured",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Captured document preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(onClick = onRetake, modifier = Modifier.weight(1f)) {
                        Text("Retake")
                    }
                    Button(onClick = onUseDocument, modifier = Modifier.weight(1f)) {
                        Text("Use document")
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 3 — OCR processing spinner
// Back and outside-click are blocked. A 30 s timeout in the ViewModel ensures
// the coroutine always exits to FAILED state if ML Kit does not respond.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun OcrProcessingDialog() {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Understanding your document...",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Reading the text from your image",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 4 — review extracted text
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DocumentReviewDialog(
    extractedText: String,
    onConfirm: (String) -> Unit,
    onEditText: () -> Unit,
    onTryAgain: () -> Unit,
    onDismiss: () -> Unit
) {
    val isEmpty = extractedText.isBlank()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Review document",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Check that the text was read correctly.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (isEmpty) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "We couldn't read this document clearly.",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try a clearer photo, or enter the text manually.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onTryAgain, modifier = Modifier.fillMaxWidth()) {
                        Text("Retake photo")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = onEditText, modifier = Modifier.fillMaxWidth()) {
                        Text("Enter text manually")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                        Text("Cancel")
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Document text",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = extractedText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onConfirm(extractedText) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm text")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = onEditText, modifier = Modifier.fillMaxWidth()) {
                        Text("Edit text")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(onClick = onTryAgain, modifier = Modifier.fillMaxWidth()) {
                        Text("Try again")
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 4b — OCR recognizer failure (exception or timeout)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun OcrFailedDialog(
    onRetakePhoto: () -> Unit,
    onChooseImage: () -> Unit,
    onEnterManually: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "We couldn't read this document clearly.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Try a clearer photo, choose another image, or enter the text yourself.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onRetakePhoto, modifier = Modifier.fillMaxWidth()) {
                    Text("Retake photo")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onChooseImage, modifier = Modifier.fillMaxWidth()) {
                    Text("Choose another image")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onEnterManually, modifier = Modifier.fillMaxWidth()) {
                    Text("Enter text manually")
                }
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Cancel")
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 5 — manual text editor
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DocumentTextEditDialog(
    initialText: String,
    onTextChanged: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Edit document text",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Correct any text that was read incorrectly.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = initialText,
                    onValueChange = onTextChanged,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    placeholder = {
                        Text(
                            text = "Type or correct the document text here...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(onClick = onSave, modifier = Modifier.weight(1f)) {
                        Text("Save correction")
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 6 — success confirmation
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DocumentSavedDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Document saved",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "The document text has been saved and confirmed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Done")
                }
            }
        }
    }
}
