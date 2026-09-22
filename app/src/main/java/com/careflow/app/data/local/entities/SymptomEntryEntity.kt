package com.careflow.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.careflow.app.domain.model.SymptomEntry
import java.time.LocalDateTime

@Entity(tableName = "symptom_entries")
data class SymptomEntryEntity(
    @PrimaryKey val id: String,
    val careEpisodeId: String,
    val symptom: String,
    val severity: String?,
    val timestamp: String,
    val notes: String?
)

fun SymptomEntryEntity.toDomain(): SymptomEntry = SymptomEntry(
    id = id,
    careEpisodeId = careEpisodeId,
    symptom = symptom,
    severity = severity,
    timestamp = LocalDateTime.parse(timestamp),
    notes = notes
)

fun SymptomEntry.toEntity(): SymptomEntryEntity = SymptomEntryEntity(
    id = id,
    careEpisodeId = careEpisodeId,
    symptom = symptom,
    severity = severity,
    timestamp = timestamp.toString(),
    notes = notes
)
