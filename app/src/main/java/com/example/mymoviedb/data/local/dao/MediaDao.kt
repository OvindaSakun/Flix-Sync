package com.example.mymoviedb.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.mymoviedb.data.local.entity.MediaEntity
import com.example.mymoviedb.data.local.entity.EpisodeWatchLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media")
    fun getAllMedia(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media")
    suspend fun getAllMediaList(): List<MediaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: List<MediaEntity>)

    @Query("DELETE FROM media")
    suspend fun clearAllMedia()

    @Query("SELECT * FROM episode_watch_logs")
    suspend fun getAllWatchLogsList(): List<EpisodeWatchLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchLogs(logs: List<EpisodeWatchLogEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchLog(log: EpisodeWatchLogEntity)

    @Query("DELETE FROM episode_watch_logs")
    suspend fun clearAllWatchLogs()
    
    @Transaction
    suspend fun restoreDatabase(
        media: List<MediaEntity>,
        watchLogs: List<EpisodeWatchLogEntity>
    ) {
        clearAllMedia() // Cascades to watch logs, but we'll clear anyway just in case
        clearAllWatchLogs()
        insertMedia(media)
        insertWatchLogs(watchLogs)
    }
}
