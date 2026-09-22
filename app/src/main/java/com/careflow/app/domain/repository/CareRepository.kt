package com.careflow.app.domain.repository

import com.careflow.app.domain.model.*
import kotlinx.coroutines.flow.Flow

interface CareRepository {
    fun getCurrentCareEpisode(): Flow<CareEpisode?>
    fun getCurrentPatient(): Flow<Patient?>
    fun getMedicationsForEpisode(episodeId: String): Flow<List<Medication>>
    fun getUpcomingAppointments(episodeId: String): Flow<List<Appointment>>
    fun getTasksForEpisode(episodeId: String): Flow<List<CareTask>>
    fun getTodayTasks(episodeId: String): Flow<List<CareTask>>
    suspend fun updateTaskCompletion(taskId: String, isCompleted: Boolean)
    suspend fun addCareTask(task: CareTask)
    suspend fun addCareTasks(tasks: List<CareTask>)
    fun getRecoveryEntries(episodeId: String): Flow<List<RecoveryEntry>>
    suspend fun addRecoveryEntry(entry: RecoveryEntry)
    suspend fun addHealthDocument(document: HealthDocument)
    fun getHealthDocuments(episodeId: String): Flow<List<HealthDocument>>
    suspend fun updateDocumentProcessing(
        documentId: String,
        state: DocumentProcessingState,
        extractedText: String?,
        error: String?
    )
    suspend fun recoverStaleProcessingDocuments()
    fun getSymptomEntries(episodeId: String): Flow<List<SymptomEntry>>
    suspend fun addSymptomEntry(entry: SymptomEntry)
    fun getHealthMeasurements(episodeId: String): Flow<List<HealthMeasurement>>
    suspend fun addHealthMeasurement(entry: HealthMeasurement)
    fun getNoteEntries(episodeId: String): Flow<List<NoteEntry>>
    suspend fun addNoteEntry(entry: NoteEntry)
    suspend fun initializeMockData()
}
