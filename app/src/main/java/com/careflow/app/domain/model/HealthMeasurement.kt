package com.careflow.app.domain.model

import java.time.LocalDateTime

data class HealthMeasurement(
    val id: String,
    val careEpisodeId: String,
    val measurementType: MeasurementType,
    val value: String,
    val unit: String,
    val timestamp: LocalDateTime,
    val notes: String? = null
)

enum class MeasurementType(val displayName: String) {
    BLOOD_PRESSURE("Blood Pressure"),
    HEART_RATE("Heart Rate"),
    TEMPERATURE("Temperature"),
    BLOOD_SUGAR("Blood Sugar"),
    OXYGEN_SATURATION("Oxygen Saturation"),
    WEIGHT("Weight"),
    OTHER("Other")
}
