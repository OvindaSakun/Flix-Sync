package com.example.mymoviedb.data.repository

import com.example.mymoviedb.data.local.dao.MediaDao
import com.example.mymoviedb.data.local.entity.MediaEntity
import com.example.mymoviedb.domain.model.MediaItem
import com.example.mymoviedb.domain.model.MediaType
import com.example.mymoviedb.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val dao: MediaDao,
    private val api: com.example.mymoviedb.data.remote.TmdbApiService
) : MediaRepository {

    override suspend fun getTrendingMedia(): Flow<List<MediaItem>> = flow {
        try {
            val response = api.getTrendingMedia()
            val entities = response.results.map { dto ->
                MediaEntity(
                    id = dto.id,
                    title = dto.getDisplayTitle(),
                    overview = dto.overview ?: "",
                    posterUrl = dto.getFullPosterUrl(),
                    releaseDate = dto.getDisplayReleaseDate(),
                    mediaType = when (dto.mediaType.lowercase()) {
                        "movie" -> MediaType.MOVIE.name
                        "tv" -> MediaType.TV.name
                        else -> MediaType.MOVIE.name // default fallback
                    }
                )
            }
            dao.insertMedia(entities)
        } catch (e: Exception) {
            e.printStackTrace() // Fallback to local DB gracefully
        }

        val localData = dao.getAllMediaList()
        emit(localData.map { it.toDomain() })
    }

    override suspend fun logEpisodeWatch(mediaItemId: Int, seasonNumber: Int, episodeNumber: Int) {
        val log = com.example.mymoviedb.data.local.entity.EpisodeWatchLogEntity(
            logUuid = java.util.UUID.randomUUID().toString(),
            mediaItemId = mediaItemId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            watchedAt = System.currentTimeMillis()
        )
        dao.insertWatchLog(log)
    }
}
