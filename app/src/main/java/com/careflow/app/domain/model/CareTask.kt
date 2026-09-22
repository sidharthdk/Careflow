package com.careflow.app.domain.model

import java.time.LocalDateTime

data class CareTask(
    val id: String,
    val careEpisodeId: String,
    val title: String,
    val description: String,
    val scheduledTime: LocalDateTime,
    val isCompleted: Boolean = false,
    val completedAt: LocalDateTime? = null,
    val taskType: TaskType = TaskType.MEDICATION
)

enum class TaskType {
    MEDICATION,
    MEASUREMENT,
    CHECKIN,
    APPOINTMENT,
    OTHER
}
