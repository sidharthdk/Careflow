package com.careflow.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.careflow.app.domain.model.FeelingLevel
import com.careflow.app.domain.model.RecoveryEntry
import java.time.LocalDateTime

@Entity(tableName = "recovery_entries")
data class RecoveryEntryEntity(
    @PrimaryKey val id: String,
    val careEpisodeId: String,
    val timestamp: String, // ISO-8601 format
    val feeling: String,
    val symptoms: String, // Comma-separated
    val notes: String? = null
)

fun RecoveryEntryEntity.toDomain(): RecoveryEntry {
    return RecoveryEntry(
        id = id,
        careEpisodeId = careEpisodeId,
        timestamp = LocalDateTime.parse(timestamp),
        feeling = FeelingLevel.valueOf(feeling),
        symptoms = if (symptoms.isBlank()) emptyList() else symptoms.split(","),
        notes = notes
    )
}

fun RecoveryEntry.toEntity(): RecoveryEntryEntity {
    return RecoveryEntryEntity(
        id = id,
        careEpisodeId = careEpisodeId,
        timestamp = timestamp.toString(),
        feeling = feeling.name,
        symptoms = symptoms.joinToString(","),
        notes = notes
    )
}
