package com.example.flixsync.data.remote
import com.example.flixsync.data.remote.dto.*
import retrofit2.http.*
interface TmdbApiService {
 @GET("3/trending/all/day") suspend fun trending():TrendingResponseDto
 @GET("3/search/multi") suspend fun search(@Query("query") query:String):TrendingResponseDto
 @GET("3/movie/{id}") suspend fun movie(@Path("id") id:Int):DetailDto
 @GET("3/tv/{id}") suspend fun tv(@Path("id") id:Int):DetailDto
 @GET("3/tv/{id}/season/{season}") suspend fun season(@Path("id") id:Int,@Path("season") season:Int):SeasonDto
}
