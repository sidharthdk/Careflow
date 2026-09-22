package com.careflow.app.domain.model

data class Medication(
    val id: String,
    val careEpisodeId: String,
    val name: String,
    val dosage: String,
    val frequency: String,
    val timing: String,
    val durationDays: Int,
    val instructions: String? = null
)
