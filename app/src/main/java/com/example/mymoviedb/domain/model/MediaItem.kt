package com.example.mymoviedb.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MediaItem(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val releaseDate: String,
    val mediaType: MediaType
)