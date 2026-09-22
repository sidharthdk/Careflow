package com.careflow.app.data.local.dao

import androidx.room.*
import com.careflow.app.data.local.entities.NoteEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteEntryDao {
    @Query("SELECT * FROM note_entries WHERE careEpisodeId = :episodeId ORDER BY timestamp DESC")
    fun getEntriesForEpisode(episodeId: String): Flow<List<NoteEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: NoteEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: NoteEntryEntity)
}
