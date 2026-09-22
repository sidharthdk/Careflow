package com.careflow.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.careflow.app.domain.model.CareTask
import com.careflow.app.domain.model.TaskType
import java.time.LocalDateTime

@Entity(tableName = "care_tasks")
data class CareTaskEntity(
    @PrimaryKey val id: String,
    val careEpisodeId: String,
    val title: String,
    val description: String,
    val scheduledTime: String, // ISO-8601 format
    val isCompleted: Boolean = false,
    val completedAt: String? = null, // ISO-8601 format
    val taskType: String = TaskType.MEDICATION.name
)

fun CareTaskEntity.toDomain(): CareTask {
    return CareTask(
        id = id,
        careEpisodeId = careEpisodeId,
        title = title,
        description = description,
        scheduledTime = LocalDateTime.parse(scheduledTime),
        isCompleted = isCompleted,
        completedAt = completedAt?.let { LocalDateTime.parse(it) },
        taskType = TaskType.valueOf(taskType)
    )
}

fun CareTask.toEntity(): CareTaskEntity {
    return CareTaskEntity(
        id = id,
        careEpisodeId = careEpisodeId,
        title = title,
        description = description,
        scheduledTime = scheduledTime.toString(),
        isCompleted = isCompleted,
        completedAt = completedAt?.toString(),
        taskType = taskType.name
    )
}
