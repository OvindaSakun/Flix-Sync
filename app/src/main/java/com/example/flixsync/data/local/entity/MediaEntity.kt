package com.example.flixsync.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.flixsync.domain.model.MediaItem
import com.example.flixsync.domain.model.MediaType

@Entity(tableName = "media", indices = [Index(value = ["tmdbId", "mediaType"], unique = true)])
data class MediaEntity(@PrimaryKey val mediaKey: String, val tmdbId: Int, val mediaType: String, val title: String, val overview: String, val posterUrl: String, val backdropUrl: String, val releaseDate: String, val isAnime: Boolean, val runtimeMinutes: Int?, val inLibrary: Boolean, val updatedAt: Long) {
    fun toDomain() = MediaItem(tmdbId, title, overview, posterUrl, backdropUrl, releaseDate, MediaType.valueOf(mediaType), isAnime, runtimeMinutes, inLibrary)
    companion object { fun fromDomain(item: MediaItem, library: Boolean = item.inLibrary) = MediaEntity(item.key, item.id, if(item.mediaType==MediaType.MOVIE) MediaType.MOVIE.name else MediaType.TV.name, item.title, item.overview, item.posterUrl, item.backdropUrl, item.releaseDate, item.isAnime||item.mediaType==MediaType.ANIME, item.runtimeMinutes, library, System.currentTimeMillis()) }
}
