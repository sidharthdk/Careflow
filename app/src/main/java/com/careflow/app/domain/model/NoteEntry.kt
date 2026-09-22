package com.careflow.app.domain.model

import java.time.LocalDateTime

data class NoteEntry(
    val id: String,
    val careEpisodeId: String,
    val content: String,
    val timestamp: LocalDateTime
)
