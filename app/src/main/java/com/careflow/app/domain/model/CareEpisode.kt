package com.careflow.app.domain.model

import java.time.LocalDate

data class CareEpisode(
    val id: String,
    val patientId: String,
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val currentDay: Int
) {
    val totalDays: Int
        get() = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
    
    val progress: Float
        get() = if (totalDays > 0) currentDay.toFloat() / totalDays.toFloat() else 0f
}
