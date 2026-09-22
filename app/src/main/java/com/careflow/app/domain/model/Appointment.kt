package com.careflow.app.domain.model

import java.time.LocalDateTime

data class Appointment(
    val id: String,
    val careEpisodeId: String,
    val title: String,
    val doctorName: String? = null,
    val location: String? = null,
    val scheduledTime: LocalDateTime,
    val notes: String? = null
)
