package com.example.mymoviedb.data.remote

import com.example.mymoviedb.data.remote.dto.TrendingResponseDto
import retrofit2.http.GET

interface TmdbApiService {
    @GET("3/trending/all/day")
    suspend fun getTrendingMedia(): TrendingResponseDto
}
