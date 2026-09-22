package com.careflow.app.domain.model

import java.time.LocalDateTime

data class SymptomEntry(
    val id: String,
    val careEpisodeId: String,
    val symptom: String,
    val severity: String? = null,
    val timestamp: LocalDateTime,
    val notes: String? = null
)
