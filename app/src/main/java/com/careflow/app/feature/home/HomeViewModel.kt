package com.careflow.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careflow.app.domain.model.Appointment
import com.careflow.app.domain.model.CareEpisode
import com.careflow.app.domain.model.CareTask
import com.careflow.app.domain.repository.CareRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val greeting: String = "",
    val todayTasks: List<CareTask> = emptyList(),
    val currentEpisode: CareEpisode? = null,
    val nextAppointment: Appointment? = null,
    val completedTasksCount: Int = 0,
    val totalTasksCount: Int = 0,
    val isLoading: Boolean = true,
    val showAddHealthUpdateDialog: Boolean = false
)

class HomeViewModel(
    private val repository: CareRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                repository.getCurrentCareEpisode(),
                repository.getTodayTasks("episode_1"),
                repository.getUpcomingAppointments("episode_1")
            ) { episode, tasks, appointments ->
                Triple(episode, tasks, appointments)
            }.collect { (episode, tasks, appointments) ->
                val completed = tasks.count { it.isCompleted }
                _uiState.update { state ->
                    state.copy(
                        greeting = getGreeting(),
                        currentEpisode = episode,
                        todayTasks = tasks,
                        nextAppointment = appointments.firstOrNull(),
                        completedTasksCount = completed,
                        totalTasksCount = tasks.size,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTaskCompletion(taskId, isCompleted)
        }
    }

    fun showAddHealthUpdateDialog() {
        _uiState.update { it.copy(showAddHealthUpdateDialog = true) }
    }

    fun hideAddHealthUpdateDialog() {
        _uiState.update { it.copy(showAddHealthUpdateDialog = false) }
    }

    private fun getGreeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }
}
