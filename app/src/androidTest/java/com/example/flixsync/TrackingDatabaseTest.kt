package com.example.flixsync
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import com.example.flixsync.data.local.AppDatabase
import com.example.flixsync.data.local.entity.*
import com.example.flixsync.domain.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class) class TrackingDatabaseTest {
 private lateinit var db:AppDatabase;private val dao get()=db.mediaDao
 private fun entity(id:Int,type:MediaType,title:String="Title")=MediaEntity.fromDomain(MediaItem(id,title,mediaType=type),true)
 @Before fun setup(){db=Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),AppDatabase::class.java).allowMainThreadQueries().build()}
 @After fun close(){db.close()}
 @Test fun unwatching_one_movie_does_not_affect_another()=runTest{val a=entity(1,MediaType.MOVIE);val b=entity(2,MediaType.MOVIE);dao.markWatched(a,0,0);dao.markWatched(b,0,0);dao.deleteWatchLog(a.mediaKey,0,0);assertEquals(listOf(b.mediaKey),dao.getAllWatchLogsList().map{it.mediaKey})}
 @Test fun metadata_refresh_preserves_history()=runTest{val a=entity(1,MediaType.TV,"Old");dao.markWatched(a,1,1);dao.upsertMetadata(a.copy(title="New",inLibrary=false));assertEquals(1,dao.getAllWatchLogsList().size);assertTrue(dao.getMedia(a.mediaKey)!!.inLibrary)}
 @Test fun repeated_episode_marking_is_idempotent()=runTest{val a=entity(1,MediaType.TV);repeat(5){dao.markWatched(a,1,1)};assertEquals(1,dao.getAllWatchLogsList().size)}
 @Test fun movie_and_tv_numeric_ids_coexist()=runTest{dao.addToLibrary(entity(9,MediaType.MOVIE));dao.addToLibrary(entity(9,MediaType.TV));assertEquals(2,dao.observeLibrary().first().size)}
 @Test fun transactional_restore_preserves_tracking()=runTest{val a=entity(3,MediaType.TV);val ep=EpisodeEntity(a.mediaKey,1,1,"Pilot",24);val log=EpisodeWatchLogEntity(a.mediaKey,1,1,10);dao.restore(listOf(a),listOf(ep),listOf(log));assertEquals(1,dao.getAllMediaList().size);assertEquals(1,dao.getAllEpisodesList().size);assertEquals(1,dao.getAllWatchLogsList().size)}
 @Test fun migration_preserves_and_deduplicates_tracking()=runTest{
  val context=ApplicationProvider.getApplicationContext<Context>();val name="migration-test.db";context.deleteDatabase(name)
  val helper=FrameworkSQLiteOpenHelperFactory().create(SupportSQLiteOpenHelper.Configuration.builder(context).name(name).callback(object:SupportSQLiteOpenHelper.Callback(1){override fun onCreate(s:SupportSQLiteDatabase){s.execSQL("CREATE TABLE media (id INTEGER NOT NULL PRIMARY KEY,title TEXT NOT NULL,overview TEXT NOT NULL,posterUrl TEXT NOT NULL,releaseDate TEXT NOT NULL,mediaType TEXT NOT NULL)");s.execSQL("CREATE TABLE episode_watch_logs (logUuid TEXT NOT NULL PRIMARY KEY,mediaItemId INTEGER NOT NULL,seasonNumber INTEGER NOT NULL,episodeNumber INTEGER NOT NULL,watchedAt INTEGER NOT NULL,FOREIGN KEY(mediaItemId) REFERENCES media(id) ON DELETE CASCADE)")};override fun onUpgrade(db:SupportSQLiteDatabase,oldVersion:Int,newVersion:Int){}}).build())
  helper.writableDatabase.apply{execSQL("INSERT INTO media VALUES(5,'Legacy','','','','TV')");execSQL("INSERT INTO episode_watch_logs VALUES('a',5,1,1,10)");execSQL("INSERT INTO episode_watch_logs VALUES('b',5,1,1,20)")};helper.close()
  val migrated=Room.databaseBuilder(context,AppDatabase::class.java,name).addMigrations(AppDatabase.MIGRATION_1_2).build();assertEquals("tv:5",migrated.mediaDao.getAllWatchLogsList().single().mediaKey);migrated.close();context.deleteDatabase(name)
 }
}
