package com.careflow.app.data.repository

import com.careflow.app.data.local.dao.*
import com.careflow.app.data.local.entities.toEntity
import com.careflow.app.data.local.entities.toDomain
import com.careflow.app.domain.model.DocumentProcessingState
import com.careflow.app.domain.model.*
import com.careflow.app.domain.repository.CareRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class CareRepositoryImpl(
    private val careTaskDao: CareTaskDao,
    private val recoveryEntryDao: RecoveryEntryDao,
    private val healthDocumentDao: HealthDocumentDao,
    private val symptomEntryDao: SymptomEntryDao,
    private val healthMeasurementDao: HealthMeasurementDao,
    private val noteEntryDao: NoteEntryDao
) : CareRepository {

    private val mockEpisodeId = "episode_1"
    private val mockPatientId = "patient_1"

    private val mockCareEpisode = CareEpisode(
        id = mockEpisodeId,
        patientId = mockPatientId,
        title = "Post-discharge recovery",
        startDate = LocalDate.of(2026, 9, 19),
        endDate = LocalDate.of(2026, 10, 3),
        currentDay = 4
    )

    private val mockPatient = Patient(
        id = mockPatientId,
        name = "Arun",
        age = 35,
        preferredLanguage = "English"
    )

    private val mockMedications = listOf(
        Medication(
            id = "med_1",
            careEpisodeId = mockEpisodeId,
            name = "Medicine A",
            dosage = "1 tablet",
            frequency = "Twice daily",
            timing = "After food",
            durationDays = 5
        ),
        Medication(
            id = "med_2",
            careEpisodeId = mockEpisodeId,
            name = "Medicine B",
            dosage = "1 tablet",
            frequency = "Once daily",
            timing = "After dinner",
            durationDays = 14
        )
    )

    private val mockAppointment = Appointment(
        id = "appt_1",
        careEpisodeId = mockEpisodeId,
        title = "Follow-up appointment",
        doctorName = "Dr. Sharma",
        location = "City Hospital",
        scheduledTime = LocalDateTime.of(2026, 9, 29, 10, 0),
        notes = "Bring all reports"
    )

    override fun getCurrentCareEpisode(): Flow<CareEpisode?> = flowOf(mockCareEpisode)
    override fun getCurrentPatient(): Flow<Patient?> = flowOf(mockPatient)
    override fun getMedicationsForEpisode(episodeId: String): Flow<List<Medication>> = flowOf(mockMedications)
    override fun getUpcomingAppointments(episodeId: String): Flow<List<Appointment>> = flowOf(listOf(mockAppointment))

    override fun getTasksForEpisode(episodeId: String): Flow<List<CareTask>> =
        careTaskDao.getTasksForEpisode(episodeId).map { it.map { e -> e.toDomain() } }

    override fun getTodayTasks(episodeId: String): Flow<List<CareTask>> {
        val today = LocalDate.now().toString()
        return careTaskDao.getTasksForDate(episodeId, today).map { it.map { e -> e.toDomain() } }
    }

    override suspend fun updateTaskCompletion(taskId: String, isCompleted: Boolean) {
        val entities = careTaskDao.getTasksForEpisode(mockEpisodeId).first()
        val target = entities.find { it.id == taskId } ?: return
        careTaskDao.updateTask(
            target.copy(
                isCompleted = isCompleted,
                completedAt = if (isCompleted) LocalDateTime.now().toString() else null
            )
        )
    }

    override suspend fun addCareTask(task: CareTask) {
        careTaskDao.insertTask(task.toEntity())
    }

    override suspend fun addCareTasks(tasks: List<CareTask>) {
        careTaskDao.insertTasks(tasks.map { it.toEntity() })
    }

    override fun getRecoveryEntries(episodeId: String): Flow<List<RecoveryEntry>> =
        recoveryEntryDao.getEntriesForEpisode(episodeId).map { it.map { e -> e.toDomain() } }

    override suspend fun addRecoveryEntry(entry: RecoveryEntry) {
        recoveryEntryDao.insertEntry(entry.toEntity())
    }

    override suspend fun addHealthDocument(document: HealthDocument) {
        healthDocumentDao.insertDocument(document.toEntity())
    }

    override fun getHealthDocuments(episodeId: String): Flow<List<HealthDocument>> =
        healthDocumentDao.getDocumentsForEpisode(episodeId).map { it.map { e -> e.toDomain() } }

    override suspend fun updateDocumentProcessing(
        documentId: String,
        state: DocumentProcessingState,
        extractedText: String?,
        error: String?
    ) {
        healthDocumentDao.updateProcessingResult(documentId, state.name, extractedText, error)
    }

    override suspend fun recoverStaleProcessingDocuments() {
        val stale = healthDocumentDao.getDocumentsByState(DocumentProcessingState.PROCESSING.name)
        for (doc in stale) {
            healthDocumentDao.updateProcessingResult(
                documentId = doc.id,
                state = DocumentProcessingState.FAILED.name,
                text = null,
                error = "Processing was interrupted"
            )
        }
    }

    override fun getSymptomEntries(episodeId: String): Flow<List<SymptomEntry>> =
        symptomEntryDao.getEntriesForEpisode(episodeId).map { it.map { e -> e.toDomain() } }

    override suspend fun addSymptomEntry(entry: SymptomEntry) {
        symptomEntryDao.insertEntry(entry.toEntity())
    }

    override fun getHealthMeasurements(episodeId: String): Flow<List<HealthMeasurement>> =
        healthMeasurementDao.getEntriesForEpisode(episodeId).map { it.map { e -> e.toDomain() } }

    override suspend fun addHealthMeasurement(entry: HealthMeasurement) {
        healthMeasurementDao.insertEntry(entry.toEntity())
    }

    override fun getNoteEntries(episodeId: String): Flow<List<NoteEntry>> =
        noteEntryDao.getEntriesForEpisode(episodeId).map { it.map { e -> e.toDomain() } }

    override suspend fun addNoteEntry(entry: NoteEntry) {
        noteEntryDao.insertEntry(entry.toEntity())
    }

    override suspend fun initializeMockData() {
        val existingCount = careTaskDao.countTasksForEpisode(mockEpisodeId)
        if (existingCount > 0) return

        val today = LocalDate.now()
        val seedTasks = listOf(
            CareTask(
                id = "task_morning_med",
                careEpisodeId = mockEpisodeId,
                title = "Morning medication",
                description = "1 tablet • After breakfast",
                scheduledTime = today.atTime(8, 0),
                taskType = TaskType.MEDICATION
            ),
            CareTask(
                id = "task_bp_check",
                careEpisodeId = mockEpisodeId,
                title = "Blood pressure check",
                description = "Record your reading",
                scheduledTime = today.atTime(10, 0),
                taskType = TaskType.MEASUREMENT
            ),
            CareTask(
                id = "task_checkin",
                careEpisodeId = mockEpisodeId,
                title = "Recovery check-in",
                description = "How are you feeling?",
                scheduledTime = today.atTime(13, 0),
                taskType = TaskType.CHECKIN
            ),
            CareTask(
                id = "task_evening_med",
                careEpisodeId = mockEpisodeId,
                title = "Evening medication",
                description = "1 tablet • After dinner",
                scheduledTime = today.atTime(20, 0),
                taskType = TaskType.MEDICATION
            )
        )
        careTaskDao.insertTasks(seedTasks.map { it.toEntity() })

        val existingRecovery = recoveryEntryDao.countEntriesForEpisode(mockEpisodeId)
        if (existingRecovery > 0) return

        val seedRecovery = listOf(
            RecoveryEntry(
                id = "rec_day1",
                careEpisodeId = mockEpisodeId,
                timestamp = LocalDateTime.of(2026, 9, 19, 18, 0),
                feeling = FeelingLevel.OKAY,
                symptoms = listOf("Pain"),
                notes = "Discharged from hospital"
            ),
            RecoveryEntry(
                id = "rec_day2",
                careEpisodeId = mockEpisodeId,
                timestamp = LocalDateTime.of(2026, 9, 20, 9, 0),
                feeling = FeelingLevel.GOOD,
                symptoms = emptyList(),
                notes = "Medication started"
            ),
            RecoveryEntry(
                id = "rec_day3",
                careEpisodeId = mockEpisodeId,
                timestamp = LocalDateTime.of(2026, 9, 21, 14, 0),
                feeling = FeelingLevel.OKAY,
                symptoms = listOf("Pain"),
                notes = "Pain reported"
            ),
            RecoveryEntry(
                id = "rec_day4",
                careEpisodeId = mockEpisodeId,
                timestamp = LocalDateTime.of(2026, 9, 22, 10, 0),
                feeling = FeelingLevel.GOOD,
                symptoms = emptyList(),
                notes = "Medication completed"
            )
        )
        seedRecovery.forEach { recoveryEntryDao.insertEntry(it.toEntity()) }
    }
}
