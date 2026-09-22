package com.careflow.app.feature.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careflow.app.domain.model.*
import com.careflow.app.domain.repository.CareRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*

sealed class RecoveryTimelineItem {
    abstract val timestamp: LocalDateTime

    data class CheckIn(val entry: RecoveryEntry) : RecoveryTimelineItem() {
        override val timestamp get() = entry.timestamp
    }
    data class Symptom(val entry: SymptomEntry) : RecoveryTimelineItem() {
        override val timestamp get() = entry.timestamp
    }
    data class Measurement(val entry: HealthMeasurement) : RecoveryTimelineItem() {
        override val timestamp get() = entry.timestamp
    }
    data class Note(val entry: NoteEntry) : RecoveryTimelineItem() {
        override val timestamp get() = entry.timestamp
    }
}

data class RecoveryUiState(
    val currentEpisode: CareEpisode? = null,
    val recoveryEntries: List<RecoveryEntry> = emptyList(),
    val timelineItems: List<RecoveryTimelineItem> = emptyList(),
    val overallFeeling: String = "Good",
    val showLogDialog: Boolean = false,
    val isLoading: Boolean = true
)

class RecoveryViewModel(
    private val repository: CareRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecoveryUiState())
    val uiState: StateFlow<RecoveryUiState> = _uiState.asStateFlow()

    init {
        loadRecoveryData()
    }

    private fun loadRecoveryData() {
        viewModelScope.launch {
            // Use nested combines to avoid dependency on the 5-flow combine overload
            val episodeAndEntries = combine(
                repository.getCurrentCareEpisode(),
                repository.getRecoveryEntries("episode_1")
            ) { ep, rec -> Pair(ep, rec) }

            val supplemental = combine(
                repository.getSymptomEntries("episode_1"),
                repository.getHealthMeasurements("episode_1"),
                repository.getNoteEntries("episode_1")
            ) { sym, meas, nts -> Triple(sym, meas, nts) }

            combine(episodeAndEntries, supplemental) { (episode, entries), (symptoms, measurements, notes) ->
                val timeline = buildList<RecoveryTimelineItem> {
                    entries.forEach { add(RecoveryTimelineItem.CheckIn(it)) }
                    symptoms.forEach { add(RecoveryTimelineItem.Symptom(it)) }
                    measurements.forEach { add(RecoveryTimelineItem.Measurement(it)) }
                    notes.forEach { add(RecoveryTimelineItem.Note(it)) }
                }.sortedByDescending { it.timestamp }
                RecoveryUiState(
                    currentEpisode = episode,
                    recoveryEntries = entries,
                    timelineItems = timeline,
                    overallFeeling = calculateOverallFeeling(entries),
                    showLogDialog = _uiState.value.showLogDialog,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.update { it.copy(
                    currentEpisode = newState.currentEpisode,
                    recoveryEntries = newState.recoveryEntries,
                    timelineItems = newState.timelineItems,
                    overallFeeling = newState.overallFeeling,
                    isLoading = false
                ) }
            }
        }
    }

    private fun calculateOverallFeeling(entries: List<RecoveryEntry>): String {
        if (entries.isEmpty()) return "Good"
        val recentEntries = entries.sortedByDescending { it.timestamp }.take(3)
        val avgFeeling = recentEntries.map { feelingToScore(it.feeling) }.average()
        return when {
            avgFeeling >= 4.0 -> "Great"
            avgFeeling >= 3.0 -> "Good"
            avgFeeling >= 2.0 -> "Okay"
            else -> "Not great"
        }
    }

    private fun feelingToScore(feeling: FeelingLevel): Int = when (feeling) {
        FeelingLevel.GREAT -> 5
        FeelingLevel.GOOD -> 4
        FeelingLevel.OKAY -> 3
        FeelingLevel.NOT_GREAT -> 2
        FeelingLevel.POOR -> 1
    }

    fun showLogRecoveryDialog() { _uiState.update { it.copy(showLogDialog = true) } }
    fun hideLogRecoveryDialog() { _uiState.update { it.copy(showLogDialog = false) } }

    fun saveRecoveryEntry(feeling: FeelingLevel, symptoms: List<String>, notes: String) {
        viewModelScope.launch {
            repository.addRecoveryEntry(
                RecoveryEntry(
                    id = UUID.randomUUID().toString(),
                    careEpisodeId = "episode_1",
                    timestamp = LocalDateTime.now(),
                    feeling = feeling,
                    symptoms = symptoms,
                    notes = notes.ifBlank { null }
                )
            )
        }
    }
}
