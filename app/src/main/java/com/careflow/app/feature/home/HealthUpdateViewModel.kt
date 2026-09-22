package com.careflow.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careflow.app.domain.model.HealthMeasurement
import com.careflow.app.domain.model.MeasurementType
import com.careflow.app.domain.model.NoteEntry
import com.careflow.app.domain.model.SymptomEntry
import com.careflow.app.domain.repository.CareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

sealed class HealthUpdateDialog {
    object None : HealthUpdateDialog()
    object Symptom : HealthUpdateDialog()
    object Measurement : HealthUpdateDialog()
    object Note : HealthUpdateDialog()
}

data class HealthUpdateUiState(
    val activeDialog: HealthUpdateDialog = HealthUpdateDialog.None,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class HealthUpdateViewModel(
    private val repository: CareRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthUpdateUiState())
    val uiState: StateFlow<HealthUpdateUiState> = _uiState.asStateFlow()

    fun showSymptomDialog() {
        _uiState.update { it.copy(activeDialog = HealthUpdateDialog.Symptom, errorMessage = null) }
    }

    fun showMeasurementDialog() {
        _uiState.update { it.copy(activeDialog = HealthUpdateDialog.Measurement, errorMessage = null) }
    }

    fun showNoteDialog() {
        _uiState.update { it.copy(activeDialog = HealthUpdateDialog.Note, errorMessage = null) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(activeDialog = HealthUpdateDialog.None, isSaving = false, errorMessage = null) }
    }

    fun saveSymptom(symptom: String, severity: String?, notes: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                repository.addSymptomEntry(
                    SymptomEntry(
                        id = UUID.randomUUID().toString(),
                        careEpisodeId = "episode_1",
                        symptom = symptom,
                        severity = severity?.takeIf { it.isNotBlank() },
                        timestamp = LocalDateTime.now(),
                        notes = notes.ifBlank { null }
                    )
                )
                _uiState.update { it.copy(activeDialog = HealthUpdateDialog.None, isSaving = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = "Failed to save symptom") }
            }
        }
    }

    fun saveMeasurement(type: MeasurementType, value: String, unit: String, notes: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                repository.addHealthMeasurement(
                    HealthMeasurement(
                        id = UUID.randomUUID().toString(),
                        careEpisodeId = "episode_1",
                        measurementType = type,
                        value = value,
                        unit = unit,
                        timestamp = LocalDateTime.now(),
                        notes = notes.ifBlank { null }
                    )
                )
                _uiState.update { it.copy(activeDialog = HealthUpdateDialog.None, isSaving = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = "Failed to save measurement") }
            }
        }
    }

    fun saveNote(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                repository.addNoteEntry(
                    NoteEntry(
                        id = UUID.randomUUID().toString(),
                        careEpisodeId = "episode_1",
                        content = content,
                        timestamp = LocalDateTime.now()
                    )
                )
                _uiState.update { it.copy(activeDialog = HealthUpdateDialog.None, isSaving = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = "Failed to save note") }
            }
        }
    }
}
