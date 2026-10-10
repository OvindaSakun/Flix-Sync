package com.example.flixsync.data.remote.dto
import com.google.gson.annotations.SerializedName
data class TrendingResponseDto(val results:List<MediaDto>)
data class MediaDto(val id:Int,val title:String?,val name:String?,val overview:String?,@SerializedName("poster_path") val posterPath:String?,@SerializedName("backdrop_path") val backdropPath:String?,@SerializedName("release_date") val releaseDate:String?,@SerializedName("first_air_date") val firstAirDate:String?,@SerializedName("media_type") val mediaType:String?,@SerializedName("genre_ids") val genreIds:List<Int> = emptyList())
data class DetailDto(val id:Int,val title:String?,val name:String?,val overview:String?,@SerializedName("poster_path") val posterPath:String?,@SerializedName("backdrop_path") val backdropPath:String?,@SerializedName("release_date") val releaseDate:String?,@SerializedName("first_air_date") val firstAirDate:String?,val runtime:Int?,@SerializedName("episode_run_time") val episodeRuntime:List<Int> = emptyList(),val seasons:List<SeasonSummaryDto> = emptyList(),val genres:List<GenreDto> = emptyList())
data class GenreDto(val id:Int,val name:String)
data class SeasonSummaryDto(@SerializedName("season_number") val seasonNumber:Int,@SerializedName("episode_count") val episodeCount:Int)
data class SeasonDto(val episodes:List<EpisodeDto>)
data class EpisodeDto(@SerializedName("season_number") val seasonNumber:Int,@SerializedName("episode_number") val episodeNumber:Int,val name:String?,val runtime:Int?)
fun imageUrl(path:String?,size:String="w500")=path?.let{"https://image.tmdb.org/t/p/$size$it"}.orEmpty()
