package com.careflow.app.data.local.dao

import androidx.room.*
import com.careflow.app.data.local.entities.CareTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CareTaskDao {

    @Query("SELECT * FROM care_tasks WHERE careEpisodeId = :episodeId ORDER BY scheduledTime ASC")
    fun getTasksForEpisode(episodeId: String): Flow<List<CareTaskEntity>>

    /**
     * DATE() comparison works because scheduledTime is stored as ISO-8601 (yyyy-MM-ddTHH:mm:ss).
     * SQLite's DATE() function correctly parses that format.
     */
    @Query(
        "SELECT * FROM care_tasks " +
        "WHERE careEpisodeId = :episodeId AND DATE(scheduledTime) = DATE(:date) " +
        "ORDER BY scheduledTime ASC"
    )
    fun getTasksForDate(episodeId: String, date: String): Flow<List<CareTaskEntity>>

    /** Used to guard duplicate seed data insertion on first launch. */
    @Query("SELECT COUNT(*) FROM care_tasks WHERE careEpisodeId = :episodeId")
    suspend fun countTasksForEpisode(episodeId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: CareTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<CareTaskEntity>)

    @Update
    suspend fun updateTask(task: CareTaskEntity)

    @Query("DELETE FROM care_tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)
}
