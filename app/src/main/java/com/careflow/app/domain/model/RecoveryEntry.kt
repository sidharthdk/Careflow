package com.careflow.app.domain.model

import java.time.LocalDateTime

data class RecoveryEntry(
    val id: String,
    val careEpisodeId: String,
    val timestamp: LocalDateTime,
    val feeling: FeelingLevel,
    val symptoms: List<String> = emptyList(),
    val notes: String? = null
)

enum class FeelingLevel(val displayName: String) {
    GREAT("Great"),
    GOOD("Good"),
    OKAY("Okay"),
    NOT_GREAT("Not great"),
    POOR("Poor")
}
