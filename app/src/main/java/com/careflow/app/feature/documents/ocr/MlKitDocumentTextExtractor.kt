package com.careflow.app.feature.documents.ocr

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class MlKitDocumentTextExtractor : DocumentTextExtractor {

    override suspend fun extractText(imageUri: Uri, context: Context): Result<String> =
        // InputImage.fromFilePath decodes the image file synchronously; run it on IO to
        // avoid blocking the main thread with a potentially large camera JPEG.
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                try {
                    val image = InputImage.fromFilePath(context, imageUri)
                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                    // NOTE: ML Kit's Task does not expose a cancellation API. Calling
                    // recognizer.close() stops future work and releases resources, but the
                    // in-flight native pipeline will continue until it finishes. The callbacks
                    // will still fire after close(); calling continuation.resume() on an already-
                    // cancelled continuation is a safe no-op in Kotlin coroutines.
                    continuation.invokeOnCancellation { recognizer.close() }

                    recognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            recognizer.close()
                            continuation.resume(Result.success(visionText.text))
                        }
                        .addOnFailureListener { e ->
                            recognizer.close()
                            continuation.resume(Result.failure(e))
                        }
                } catch (e: Exception) {
                    continuation.resume(Result.failure(e))
                }
            }
        }
}
