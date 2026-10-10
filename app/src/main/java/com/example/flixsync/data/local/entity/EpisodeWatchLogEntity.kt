package com.example.flixsync.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.flixsync.domain.model.EpisodeWatchLog

@Entity(tableName = "watch_logs", primaryKeys = ["mediaKey", "seasonNumber", "episodeNumber"], foreignKeys = [ForeignKey(entity = MediaEntity::class, parentColumns = ["mediaKey"], childColumns = ["mediaKey"], onDelete = ForeignKey.CASCADE)], indices = [Index("mediaKey")])
data class EpisodeWatchLogEntity(val mediaKey: String, val seasonNumber: Int, val episodeNumber: Int, val watchedAt: Long) {
    fun toDomain() = EpisodeWatchLog(mediaKey = mediaKey, mediaItemId = mediaKey.substringAfter(':').toIntOrNull() ?: 0, seasonNumber = seasonNumber, episodeNumber = episodeNumber, watchedAt = watchedAt)
}
