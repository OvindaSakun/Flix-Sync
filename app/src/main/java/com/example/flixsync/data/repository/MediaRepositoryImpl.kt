package com.example.flixsync.data.repository
import com.example.flixsync.data.local.dao.MediaDao
import com.example.flixsync.data.local.entity.*
import com.example.flixsync.data.remote.TmdbApiService
import com.example.flixsync.data.remote.dto.*
import com.example.flixsync.domain.model.*
import com.example.flixsync.domain.repository.MediaRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(private val dao:MediaDao,private val api:TmdbApiService):MediaRepository {
 override val library:Flow<List<TrackedMedia>> = combine(dao.observeLibrary(),dao.observeEpisodes(),dao.observeWatchLogs()){media,eps,logs-> media.map{m->TrackedMedia(m.toDomain(),eps.filter{it.mediaKey==m.mediaKey}.map{it.toDomain()},logs.filter{it.mediaKey==m.mediaKey}.map{it.seasonNumber to it.episodeNumber}.toSet())}}
 override val watchedMovieCount=dao.observeWatchLogs().map{it.count{l->l.mediaKey.startsWith("movie:")}}
 override val watchedEpisodeCount=dao.observeWatchLogs().map{it.count{l->l.mediaKey.startsWith("tv:")}}
 override suspend fun discover()=api.trending().results.mapNotNull{it.toMedia()}
 override suspend fun search(query:String)=api.search(query).results.mapNotNull{it.toMedia()}
 override suspend fun loadDetails(item:MediaItem):MediaItem { val d=if(item.mediaType==MediaType.MOVIE)api.movie(item.id) else api.tv(item.id);val detailed=item.copy(title=d.title?:d.name?:item.title,overview=d.overview?:item.overview,posterUrl=imageUrl(d.posterPath),backdropUrl=imageUrl(d.backdropPath,"w780"),releaseDate=d.releaseDate?:d.firstAirDate.orEmpty(),runtimeMinutes=d.runtime?:d.episodeRuntime.firstOrNull(),isAnime=item.isAnime||d.genres.any{it.id==16});dao.upsertMetadata(MediaEntity.fromDomain(detailed));return detailed }
 override suspend fun add(item:MediaItem){dao.addToLibrary(MediaEntity.fromDomain(item,true));if(item.mediaType==MediaType.TV)refreshEpisodes(item)}
 override suspend fun remove(item:MediaItem,deleteHistory:Boolean){dao.removeFromLibrary(item.key,deleteHistory)}
 override suspend fun setMovieWatched(item:MediaItem,watched:Boolean){if(watched)dao.markWatched(MediaEntity.fromDomain(item,true),0,0) else dao.deleteWatchLog(item.key,0,0)}
 override suspend fun setEpisodeWatched(item:MediaItem,season:Int,episode:Int,watched:Boolean){if(watched)dao.markWatched(MediaEntity.fromDomain(item,true),season,episode) else dao.deleteWatchLog(item.key,season,episode)}
 override suspend fun refreshEpisodes(item:MediaItem){ val detail=api.tv(item.id);val all=detail.seasons.filter{it.seasonNumber>0}.flatMap{s->api.season(item.id,s.seasonNumber).episodes}.map{EpisodeEntity(item.key,it.seasonNumber,it.episodeNumber,it.name.orEmpty(),it.runtime)};dao.insertEpisodes(all) }
 private fun MediaDto.toMedia():MediaItem? { val type=when(mediaType){"movie"->MediaType.MOVIE;"tv"->MediaType.TV;else->return null};return MediaItem(id,title?:name?:"Unknown title",overview.orEmpty(),imageUrl(posterPath),imageUrl(backdropPath,"w780"),releaseDate?:firstAirDate.orEmpty(),type,genreIds.contains(16)) }
}
