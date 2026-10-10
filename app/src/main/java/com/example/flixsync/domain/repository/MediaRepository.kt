package com.example.flixsync.domain.repository
import com.example.flixsync.domain.model.*
import kotlinx.coroutines.flow.Flow
interface MediaRepository {
 val library:Flow<List<TrackedMedia>>
 val watchedMovieCount:Flow<Int>
 val watchedEpisodeCount:Flow<Int>
 suspend fun discover():List<MediaItem>
 suspend fun search(query:String):List<MediaItem>
 suspend fun loadDetails(item:MediaItem):MediaItem
 suspend fun add(item:MediaItem)
 suspend fun remove(item:MediaItem,deleteHistory:Boolean=false)
 suspend fun setMovieWatched(item:MediaItem,watched:Boolean)
 suspend fun setEpisodeWatched(item:MediaItem,season:Int,episode:Int,watched:Boolean)
 suspend fun refreshEpisodes(item:MediaItem)
}
