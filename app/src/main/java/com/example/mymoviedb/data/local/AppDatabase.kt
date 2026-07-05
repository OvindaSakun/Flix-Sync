package com.example.mymoviedb.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mymoviedb.data.local.dao.MediaDao
import com.example.mymoviedb.data.local.entity.MediaEntity
import com.example.mymoviedb.data.local.entity.EpisodeWatchLogEntity

@Database(entities = [MediaEntity::class, EpisodeWatchLogEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val mediaDao: MediaDao
}
