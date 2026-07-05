package com.example.mymoviedb.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val timestamp: Long,
    val mediaItems: List<MediaItem> = emptyList(),
    val watchLogs: List<EpisodeWatchLog> = emptyList(),
    // Placeholders for future features
    val watchlistIds: List<Int> = emptyList(),
    val settings: Map<String, String> = emptyMap()
)
