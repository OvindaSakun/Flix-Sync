package com.example.mymoviedb.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class EpisodeWatchLog(
    val logUuid: String,
    val mediaItemId: Int,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val watchedAt: Long // Timestamp in milliseconds
)
