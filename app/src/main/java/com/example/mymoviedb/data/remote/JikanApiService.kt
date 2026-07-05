package com.example.mymoviedb.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JikanApiService {
    @GET("anime")
    suspend fun searchAnime(
        @Query("q") query: String
    ): Any // To be replaced with actual DTO

    @GET("anime/{id}")
    suspend fun getAnimeDetails(
        @Path("id") id: Int
    ): Any
}
