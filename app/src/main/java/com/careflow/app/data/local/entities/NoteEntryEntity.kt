package com.careflow.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.careflow.app.domain.model.NoteEntry
import java.time.LocalDateTime

@Entity(tableName = "note_entries")
data class NoteEntryEntity(
    @PrimaryKey val id: String,
    val careEpisodeId: String,
    val content: String,
    val timestamp: String
)

fun NoteEntryEntity.toDomain(): NoteEntry = NoteEntry(
    id = id,
    careEpisodeId = careEpisodeId,
    content = content,
    timestamp = LocalDateTime.parse(timestamp)
)

fun NoteEntry.toEntity(): NoteEntryEntity = NoteEntryEntity(
    id = id,
    careEpisodeId = careEpisodeId,
    content = content,
    timestamp = timestamp.toString()
)
