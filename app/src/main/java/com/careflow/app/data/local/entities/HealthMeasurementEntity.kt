package com.careflow.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.careflow.app.domain.model.HealthMeasurement
import com.careflow.app.domain.model.MeasurementType
import java.time.LocalDateTime

@Entity(tableName = "health_measurements")
data class HealthMeasurementEntity(
    @PrimaryKey val id: String,
    val careEpisodeId: String,
    val measurementType: String,
    val value: String,
    val unit: String,
    val timestamp: String,
    val notes: String?
)

fun HealthMeasurementEntity.toDomain(): HealthMeasurement = HealthMeasurement(
    id = id,
    careEpisodeId = careEpisodeId,
    measurementType = runCatching { MeasurementType.valueOf(measurementType) }.getOrDefault(MeasurementType.OTHER),
    value = value,
    unit = unit,
    timestamp = LocalDateTime.parse(timestamp),
    notes = notes
)

fun HealthMeasurement.toEntity(): HealthMeasurementEntity = HealthMeasurementEntity(
    id = id,
    careEpisodeId = careEpisodeId,
    measurementType = measurementType.name,
    value = value,
    unit = unit,
    timestamp = timestamp.toString(),
    notes = notes
)
