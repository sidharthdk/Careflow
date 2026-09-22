package com.careflow.app.data.local.dao

import androidx.room.*
import com.careflow.app.data.local.entities.HealthDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDocumentDao {
    @Query("SELECT * FROM health_documents ORDER BY capturedAt DESC")
    fun getAllDocuments(): Flow<List<HealthDocumentEntity>>

    @Query("SELECT * FROM health_documents WHERE careEpisodeId = :episodeId ORDER BY capturedAt DESC")
    fun getDocumentsForEpisode(episodeId: String): Flow<List<HealthDocumentEntity>>

    @Query("SELECT * FROM health_documents WHERE id = :documentId LIMIT 1")
    suspend fun getDocumentById(documentId: String): HealthDocumentEntity?

    @Query("SELECT * FROM health_documents WHERE processingState = :state")
    suspend fun getDocumentsByState(state: String): List<HealthDocumentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: HealthDocumentEntity)

    @Query(
        "UPDATE health_documents SET processingState = :state, extractedText = :text, processingError = :error WHERE id = :documentId"
    )
    suspend fun updateProcessingResult(
        documentId: String,
        state: String,
        text: String?,
        error: String?
    )

    @Query("DELETE FROM health_documents WHERE id = :documentId")
    suspend fun deleteDocument(documentId: String)
}
