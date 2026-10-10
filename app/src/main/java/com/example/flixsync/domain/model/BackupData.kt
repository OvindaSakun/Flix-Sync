package com.example.flixsync.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val version: Int = 2,
    val timestamp: Long,
    val mediaItems: List<MediaItem> = emptyList(),
    val watchLogs: List<EpisodeWatchLog> = emptyList(),
    val episodes: List<Episode> = emptyList(),
    // Placeholders for future features
    val watchlistIds: List<Int> = emptyList(),
    val settings: Map<String, String> = emptyMap()
)

@Serializable
data class Episode(val mediaKey: String, val seasonNumber: Int, val episodeNumber: Int, val name: String = "", val runtimeMinutes: Int? = null)

data class TrackedMedia(val media: MediaItem, val episodes: List<Episode>, val watched: Set<Pair<Int, Int>>) {
    val watchedCount get() = watched.size
    val nextEpisode: Episode? get() = episodes.filter { it.seasonNumber > 0 }.sortedWith(compareBy(Episode::seasonNumber, Episode::episodeNumber)).firstOrNull { (it.seasonNumber to it.episodeNumber) !in watched }
    val metadataAvailable get() = media.mediaType == MediaType.MOVIE || episodes.isNotEmpty()
    val complete get() = episodes.isNotEmpty() && nextEpisode == null
}
