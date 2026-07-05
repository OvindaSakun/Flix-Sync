package com.example.mymoviedb.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TrendingResponseDto(
    @SerializedName("results")
    val results: List<MediaDto>
)

data class MediaDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("overview")
    val overview: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("first_air_date")
    val firstAirDate: String?,
    @SerializedName("media_type")
    val mediaType: String
) {
    fun getFullPosterUrl(): String {
        return if (posterPath != null) {
            "https://image.tmdb.org/t/p/w500$posterPath"
        } else {
            ""
        }
    }

    fun getDisplayTitle(): String {
        return title ?: name ?: "Unknown Title"
    }

    fun getDisplayReleaseDate(): String {
        return releaseDate ?: firstAirDate ?: ""
    }
}
