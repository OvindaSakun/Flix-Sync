package com.example.flixsync.presentation.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flixsync.domain.model.*
import com.example.flixsync.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SearchFilter { ALL, MOVIES, TV, ANIME }
data class SearchState(val query:String="",val filter:SearchFilter=SearchFilter.ALL,val items:List<MediaItem> = emptyList(),val loading:Boolean=false,val error:String?=null)
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel class HomeViewModel @Inject constructor(private val repo:MediaRepository):ViewModel(){
 val library=repo.library.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val watchedMovies=repo.watchedMovieCount.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),0)
 val watchedEpisodes=repo.watchedEpisodeCount.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),0)
 private val query=MutableStateFlow("");private val filter=MutableStateFlow(SearchFilter.ALL);private val retry=MutableStateFlow(0)
 private val raw=combine(query.debounce(350),retry){q,_->q}.flatMapLatest{q->flow{emit(Triple(true,emptyList<MediaItem>(),null as String?));try{emit(Triple(false,if(q.isBlank())repo.discover() else repo.search(q),null))}catch(e:CancellationException){throw e}catch(e:Exception){emit(Triple(false,emptyList(),e.message?:"Could not load titles"))}}}
 val search=combine(query,filter,raw){q,f,r->SearchState(q,f,r.second.filter{when(f){SearchFilter.ALL->true;SearchFilter.MOVIES->it.mediaType==MediaType.MOVIE;SearchFilter.TV->it.mediaType==MediaType.TV;SearchFilter.ANIME->it.mediaType==MediaType.TV&&it.isAnime}},r.first,r.third)}.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),SearchState(loading=true))
 private val _selected=MutableStateFlow<MediaItem?>(null);val selected=_selected.asStateFlow()
 private val _message=MutableStateFlow<String?>(null);val message=_message.asStateFlow()
 fun setQuery(v:String){query.value=v};fun setFilter(v:SearchFilter){filter.value=v};fun retry(){retry.value++}
 fun select(item:MediaItem){_selected.value=item;viewModelScope.launch{try{_selected.value=repo.loadDetails(item)}catch(e:CancellationException){throw e}catch(e:Exception){_message.value=e.message?:"Details unavailable"}}};fun closeDetails(){_selected.value=null}
 fun add(item:MediaItem)=act{repo.add(item);_selected.value=item.copy(inLibrary=true)}
 fun remove(item:MediaItem,deleteHistory:Boolean=false)=act{repo.remove(item,deleteHistory);_selected.value=item.copy(inLibrary=false)}
 fun movie(item:MediaItem,watched:Boolean)=act{repo.setMovieWatched(item,watched)}
 fun episode(item:MediaItem,s:Int,e:Int,watched:Boolean)=act{repo.setEpisodeWatched(item,s,e,watched)}
 fun next(tracked:TrackedMedia){tracked.nextEpisode?.let{episode(tracked.media,it.seasonNumber,it.episodeNumber,true)}}
 private fun act(block:suspend()->Unit)=viewModelScope.launch{try{block()}catch(e:CancellationException){throw e}catch(e:Exception){_message.value=e.message?:"Operation failed"}}
 fun clearMessage(){_message.value=null}
}
