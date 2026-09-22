package com.careflow.app.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careflow.app.domain.repository.CareRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val patientName: String = "",
    val patientAge: Int = 0,
    val preferredLanguage: String = "English"
)

class ProfileViewModel(
    private val repository: CareRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            repository.getCurrentPatient().collect { patient ->
                patient?.let {
                    _uiState.update { state ->
                        state.copy(
                            patientName = it.name,
                            patientAge = it.age,
                            preferredLanguage = it.preferredLanguage
                        )
                    }
                }
            }
        }
    }
}
