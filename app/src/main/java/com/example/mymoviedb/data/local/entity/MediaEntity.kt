package com.example.mymoviedb.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mymoviedb.domain.model.MediaItem
import com.example.mymoviedb.domain.model.MediaType

@Entity(tableName = "media")
data class MediaEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val releaseDate: String,
    val mediaType: String
) {
    fun toDomain(): MediaItem {
        return MediaItem(
            id = id,
            title = title,
            overview = overview,
            posterUrl = posterUrl,
            releaseDate = releaseDate,
            mediaType = MediaType.valueOf(mediaType)
        )
    }

    companion object {
        fun fromDomain(item: MediaItem): MediaEntity {
            return MediaEntity(
                id = item.id,
                title = item.title,
                overview = item.overview,
                posterUrl = item.posterUrl,
                releaseDate = item.releaseDate,
                mediaType = item.mediaType.name
            )
        }
    }
}