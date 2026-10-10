package com.example.flixsync.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class EpisodeWatchLog(
    val mediaKey: String = "",
    val mediaItemId: Int = 0,
    val mediaType: MediaType = MediaType.TV,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val watchedAt: Long,
    val logUuid: String = "",
)
