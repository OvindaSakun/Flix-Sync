package com.example.mymoviedb.domain.repository

import com.example.mymoviedb.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    suspend fun getTrendingMedia(): Flow<List<MediaItem>>
    suspend fun logEpisodeWatch(mediaItemId: Int, seasonNumber: Int, episodeNumber: Int)
}
