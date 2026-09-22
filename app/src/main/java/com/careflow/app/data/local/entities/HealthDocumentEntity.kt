package com.careflow.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.careflow.app.domain.model.DocumentProcessingState
import com.careflow.app.domain.model.DocumentType
import com.careflow.app.domain.model.HealthDocument
import java.time.LocalDateTime

@Entity(tableName = "health_documents")
data class HealthDocumentEntity(
    @PrimaryKey val id: String,
    val careEpisodeId: String?,
    val documentType: String,
    val title: String,
    val filePath: String,
    val capturedAt: String, // ISO-8601 format
    val isProcessed: Boolean = false,
    val processingState: String = DocumentProcessingState.CAPTURED.name,
    val extractedText: String? = null,
    val processingError: String? = null
)

fun HealthDocumentEntity.toDomain(): HealthDocument {
    return HealthDocument(
        id = id,
        careEpisodeId = careEpisodeId,
        documentType = DocumentType.valueOf(documentType),
        title = title,
        filePath = filePath,
        capturedAt = LocalDateTime.parse(capturedAt),
        isProcessed = isProcessed,
        processingState = runCatching { DocumentProcessingState.valueOf(processingState) }
            .getOrDefault(DocumentProcessingState.CAPTURED),
        extractedText = extractedText,
        processingError = processingError
    )
}

fun HealthDocument.toEntity(): HealthDocumentEntity {
    return HealthDocumentEntity(
        id = id,
        careEpisodeId = careEpisodeId,
        documentType = documentType.name,
        title = title,
        filePath = filePath,
        capturedAt = capturedAt.toString(),
        isProcessed = isProcessed,
        processingState = processingState.name,
        extractedText = extractedText,
        processingError = processingError
    )
}
