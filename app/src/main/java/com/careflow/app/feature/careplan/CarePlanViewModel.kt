package com.careflow.app.feature.careplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.careflow.app.domain.model.CareTask
import com.careflow.app.domain.repository.CareRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class CarePlanUiState(
    val morningTasks: List<CareTask> = emptyList(),
    val afternoonTasks: List<CareTask> = emptyList(),
    val eveningTasks: List<CareTask> = emptyList(),
    val upcomingTasks: List<CareTask> = emptyList(),
    val isLoading: Boolean = true
)

class CarePlanViewModel(
    private val repository: CareRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CarePlanUiState())
    val uiState: StateFlow<CarePlanUiState> = _uiState.asStateFlow()

    init {
        loadCarePlan()
    }

    private fun loadCarePlan() {
        viewModelScope.launch {
            repository.getTasksForEpisode("episode_1").collect { tasks ->
                val (morning, afternoon, evening, upcoming) = categorizeTasks(tasks)
                _uiState.update {
                    it.copy(
                        morningTasks = morning,
                        afternoonTasks = afternoon,
                        eveningTasks = evening,
                        upcomingTasks = upcoming,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun categorizeTasks(tasks: List<CareTask>): TaskCategories {
        val today = LocalDateTime.now().toLocalDate()
        val morningTasks = mutableListOf<CareTask>()
        val afternoonTasks = mutableListOf<CareTask>()
        val eveningTasks = mutableListOf<CareTask>()
        val upcomingTasks = mutableListOf<CareTask>()

        tasks.forEach { task ->
            val taskDate = task.scheduledTime.toLocalDate()
            val hour = task.scheduledTime.hour

            when {
                taskDate.isAfter(today) -> upcomingTasks.add(task)
                hour < 12 -> morningTasks.add(task)
                hour < 17 -> afternoonTasks.add(task)
                else -> eveningTasks.add(task)
            }
        }

        return TaskCategories(
            morning = morningTasks,
            afternoon = afternoonTasks,
            evening = eveningTasks,
            upcoming = upcomingTasks
        )
    }

    fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTaskCompletion(taskId, isCompleted)
        }
    }

    private data class TaskCategories(
        val morning: List<CareTask>,
        val afternoon: List<CareTask>,
        val evening: List<CareTask>,
        val upcoming: List<CareTask>
    )
}
