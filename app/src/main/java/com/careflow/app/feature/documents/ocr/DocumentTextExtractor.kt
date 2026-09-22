package com.careflow.app.feature.documents.ocr

import android.content.Context
import android.net.Uri

interface DocumentTextExtractor {
    suspend fun extractText(imageUri: Uri, context: Context): Result<String>
}
