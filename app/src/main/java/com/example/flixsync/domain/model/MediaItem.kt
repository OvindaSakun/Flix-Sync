package com.example.flixsync.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MediaItem(
    val id: Int,
    val title: String,
    val overview: String = "",
    val posterUrl: String = "",
    val backdropUrl: String = "",
    val releaseDate: String = "",
    val mediaType: MediaType,
    val isAnime: Boolean = false,
    val runtimeMinutes: Int? = null,
    val inLibrary: Boolean = false,
) { val key: String get() = "${if (mediaType == MediaType.MOVIE) "movie" else "tv"}:$id" }
