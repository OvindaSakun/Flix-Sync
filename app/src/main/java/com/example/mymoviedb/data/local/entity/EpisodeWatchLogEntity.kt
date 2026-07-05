package com.example.mymoviedb.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.mymoviedb.domain.model.EpisodeWatchLog

@Entity(
    tableName = "episode_watch_logs",
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["mediaItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["mediaItemId"])]
)
data class EpisodeWatchLogEntity(
    @PrimaryKey
    val logUuid: String,
    val mediaItemId: Int,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val watchedAt: Long
) {
    fun toDomain(): EpisodeWatchLog {
        return EpisodeWatchLog(
            logUuid = logUuid,
            mediaItemId = mediaItemId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            watchedAt = watchedAt
        )
    }

    companion object {
        fun fromDomain(log: EpisodeWatchLog): EpisodeWatchLogEntity {
            return EpisodeWatchLogEntity(
                logUuid = log.logUuid,
                mediaItemId = log.mediaItemId,
                seasonNumber = log.seasonNumber,
                episodeNumber = log.episodeNumber,
                watchedAt = log.watchedAt
            )
        }
    }
}