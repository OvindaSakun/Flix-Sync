package com.example.flixsync.data.local.dao
import androidx.room.*
import com.example.flixsync.data.local.entity.*
import kotlinx.coroutines.flow.Flow
@Dao interface MediaDao {
 @Query("SELECT * FROM media WHERE inLibrary = 1 ORDER BY title") fun observeLibrary(): Flow<List<MediaEntity>>
 @Query("SELECT * FROM watch_logs") fun observeWatchLogs(): Flow<List<EpisodeWatchLogEntity>>
 @Query("SELECT * FROM episodes") fun observeEpisodes(): Flow<List<EpisodeEntity>>
 @Query("SELECT * FROM media") suspend fun getAllMediaList(): List<MediaEntity>
 @Query("SELECT * FROM watch_logs") suspend fun getAllWatchLogsList(): List<EpisodeWatchLogEntity>
 @Query("SELECT * FROM episodes") suspend fun getAllEpisodesList(): List<EpisodeEntity>
 @Query("SELECT * FROM media WHERE mediaKey=:key") suspend fun getMedia(key:String): MediaEntity?
 @Insert(onConflict=OnConflictStrategy.IGNORE) suspend fun insertMedia(media:MediaEntity):Long
 @Update suspend fun updateMedia(media:MediaEntity)
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun insertEpisodes(items:List<EpisodeEntity>)
 @Insert(onConflict=OnConflictStrategy.IGNORE) suspend fun insertWatchLog(log:EpisodeWatchLogEntity):Long
 @Query("DELETE FROM watch_logs WHERE mediaKey=:key AND seasonNumber=:season AND episodeNumber=:episode") suspend fun deleteWatchLog(key:String,season:Int,episode:Int)
 @Query("UPDATE media SET inLibrary=:value WHERE mediaKey=:key") suspend fun setLibrary(key:String,value:Boolean)
 @Query("DELETE FROM watch_logs WHERE mediaKey=:key") suspend fun deleteHistory(key:String)
 @Query("DELETE FROM episodes") suspend fun clearEpisodes(); @Query("DELETE FROM watch_logs") suspend fun clearWatchLogs(); @Query("DELETE FROM media") suspend fun clearMedia()
 @Transaction suspend fun upsertMetadata(entity:MediaEntity){ val old=getMedia(entity.mediaKey); if(old==null) insertMedia(entity) else updateMedia(entity.copy(inLibrary=old.inLibrary)) }
 @Transaction suspend fun addToLibrary(entity:MediaEntity){ upsertMetadata(entity); setLibrary(entity.mediaKey,true) }
 @Transaction suspend fun removeFromLibrary(key:String,deleteHistory:Boolean){ setLibrary(key,false); if(deleteHistory) deleteHistory(key) }
 @Transaction suspend fun markWatched(entity:MediaEntity,season:Int,episode:Int){ addToLibrary(entity); insertWatchLog(EpisodeWatchLogEntity(entity.mediaKey,season,episode,System.currentTimeMillis())) }
 @Transaction suspend fun restore(media:List<MediaEntity>,episodes:List<EpisodeEntity>,logs:List<EpisodeWatchLogEntity>){ clearWatchLogs();clearEpisodes();clearMedia();media.forEach{insertMedia(it)};insertEpisodes(episodes);logs.forEach{insertWatchLog(it)} }
}
