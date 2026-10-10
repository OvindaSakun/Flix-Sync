package com.example.flixsync.data.local
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.flixsync.data.local.dao.MediaDao
import com.example.flixsync.data.local.entity.*
@Database(entities=[MediaEntity::class,EpisodeEntity::class,EpisodeWatchLogEntity::class],version=2,exportSchema=false)
abstract class AppDatabase:RoomDatabase(){ abstract val mediaDao:MediaDao
 companion object { val MIGRATION_1_2=object:Migration(1,2){ override fun migrate(db:SupportSQLiteDatabase){
  db.execSQL("ALTER TABLE media RENAME TO media_legacy");db.execSQL("ALTER TABLE episode_watch_logs RENAME TO watch_legacy")
  db.execSQL("CREATE TABLE IF NOT EXISTS media (mediaKey TEXT NOT NULL,tmdbId INTEGER NOT NULL,mediaType TEXT NOT NULL,title TEXT NOT NULL,overview TEXT NOT NULL,posterUrl TEXT NOT NULL,backdropUrl TEXT NOT NULL,releaseDate TEXT NOT NULL,isAnime INTEGER NOT NULL,runtimeMinutes INTEGER,inLibrary INTEGER NOT NULL,updatedAt INTEGER NOT NULL,PRIMARY KEY(mediaKey))")
  db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_media_tmdbId_mediaType ON media(tmdbId,mediaType)")
  db.execSQL("INSERT INTO media SELECT (CASE WHEN mediaType='MOVIE' THEN 'movie:' ELSE 'tv:' END)||id,id,CASE WHEN mediaType='MOVIE' THEN 'MOVIE' ELSE 'TV' END,title,overview,posterUrl,'',releaseDate,CASE WHEN mediaType='ANIME' THEN 1 ELSE 0 END,NULL,CASE WHEN EXISTS(SELECT 1 FROM watch_legacy w WHERE w.mediaItemId=media_legacy.id) THEN 1 ELSE 0 END,0 FROM media_legacy")
  db.execSQL("CREATE TABLE IF NOT EXISTS episodes (mediaKey TEXT NOT NULL,seasonNumber INTEGER NOT NULL,episodeNumber INTEGER NOT NULL,name TEXT NOT NULL,runtimeMinutes INTEGER,PRIMARY KEY(mediaKey,seasonNumber,episodeNumber),FOREIGN KEY(mediaKey) REFERENCES media(mediaKey) ON DELETE CASCADE)");db.execSQL("CREATE INDEX index_episodes_mediaKey ON episodes(mediaKey)")
  db.execSQL("CREATE TABLE IF NOT EXISTS watch_logs (mediaKey TEXT NOT NULL,seasonNumber INTEGER NOT NULL,episodeNumber INTEGER NOT NULL,watchedAt INTEGER NOT NULL,PRIMARY KEY(mediaKey,seasonNumber,episodeNumber),FOREIGN KEY(mediaKey) REFERENCES media(mediaKey) ON DELETE CASCADE)");db.execSQL("CREATE INDEX index_watch_logs_mediaKey ON watch_logs(mediaKey)")
  db.execSQL("INSERT OR IGNORE INTO watch_logs SELECT (CASE WHEN m.mediaType='MOVIE' THEN 'movie:' ELSE 'tv:' END)||w.mediaItemId,w.seasonNumber,w.episodeNumber,w.watchedAt FROM watch_legacy w JOIN media_legacy m ON m.id=w.mediaItemId")
  db.execSQL("DROP TABLE watch_legacy");db.execSQL("DROP TABLE media_legacy")
 } } }
}
