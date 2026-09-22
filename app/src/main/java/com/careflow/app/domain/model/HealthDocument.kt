package com.careflow.app.domain.model

import java.time.LocalDateTime

data class HealthDocument(
    val id: String,
    val careEpisodeId: String?,
    val documentType: DocumentType,
    val title: String,
    val filePath: String,
    val capturedAt: LocalDateTime,
    val isProcessed: Boolean = false,
    val processingState: DocumentProcessingState = DocumentProcessingState.CAPTURED,
    val extractedText: String? = null,
    val processingError: String? = null
)

enum class DocumentType(val displayName: String) {
    PRESCRIPTION("Prescription"),
    DISCHARGE_SUMMARY("Discharge Summary"),
    LAB_REPORT("Lab Report"),
    SCAN("Scan/X-ray"),
    OTHER("Other Document")
}

enum class DocumentProcessingState {
    CAPTURED,
    PROCESSING,
    TEXT_EXTRACTED,
    CONFIRMED,
    FAILED
}
