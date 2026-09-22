package com.careflow.app.data.local.dao

import androidx.room.*
import com.careflow.app.data.local.entities.HealthMeasurementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthMeasurementDao {
    @Query("SELECT * FROM health_measurements WHERE careEpisodeId = :episodeId ORDER BY timestamp DESC")
    fun getEntriesForEpisode(episodeId: String): Flow<List<HealthMeasurementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: HealthMeasurementEntity)

    @Delete
    suspend fun deleteEntry(entry: HealthMeasurementEntity)
}
