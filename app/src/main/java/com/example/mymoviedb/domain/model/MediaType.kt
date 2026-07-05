package com.example.mymoviedb.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class MediaType {
    MOVIE,
    TV,
    ANIME
}
