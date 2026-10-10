package com.example.flixsync.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.flixsync.domain.model.Episode

@Entity(tableName = "episodes", primaryKeys = ["mediaKey", "seasonNumber", "episodeNumber"], foreignKeys = [ForeignKey(entity = MediaEntity::class, parentColumns = ["mediaKey"], childColumns = ["mediaKey"], onDelete = ForeignKey.CASCADE)], indices = [Index("mediaKey")])
data class EpisodeEntity(val mediaKey: String, val seasonNumber: Int, val episodeNumber: Int, val name: String, val runtimeMinutes: Int?) { fun toDomain() = Episode(mediaKey, seasonNumber, episodeNumber, name, runtimeMinutes) }
