package com.careflow.app.data.local.dao

import androidx.room.*
import com.careflow.app.data.local.entities.SymptomEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SymptomEntryDao {
    @Query("SELECT * FROM symptom_entries WHERE careEpisodeId = :episodeId ORDER BY timestamp DESC")
    fun getEntriesForEpisode(episodeId: String): Flow<List<SymptomEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: SymptomEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: SymptomEntryEntity)
}
