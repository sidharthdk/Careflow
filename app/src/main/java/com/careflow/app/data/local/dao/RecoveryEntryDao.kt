package com.careflow.app.data.local.dao

import androidx.room.*
import com.careflow.app.data.local.entities.RecoveryEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecoveryEntryDao {

    @Query("SELECT * FROM recovery_entries WHERE careEpisodeId = :episodeId ORDER BY timestamp DESC")
    fun getEntriesForEpisode(episodeId: String): Flow<List<RecoveryEntryEntity>>

    /** Used to guard duplicate seed data insertion on first launch. */
    @Query("SELECT COUNT(*) FROM recovery_entries WHERE careEpisodeId = :episodeId")
    suspend fun countEntriesForEpisode(episodeId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: RecoveryEntryEntity)

    @Query("DELETE FROM recovery_entries WHERE id = :entryId")
    suspend fun deleteEntry(entryId: String)
}
