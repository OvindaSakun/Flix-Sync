package com.example.mymoviedb.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymoviedb.domain.model.MediaItem
import com.example.mymoviedb.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {

    private val _trendingMedia = MutableStateFlow<List<MediaItem>>(emptyList())
    val trendingMedia: StateFlow<List<MediaItem>> = _trendingMedia.asStateFlow()

    private val _episodeProgress = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val episodeProgress: StateFlow<Map<Int, Int>> = _episodeProgress.asStateFlow()

    init {
        loadTrendingMedia()
    }

    private fun loadTrendingMedia() {
        viewModelScope.launch {
            repository.getTrendingMedia().collect { items ->
                _trendingMedia.value = items
                
                // Initialize episode progress to 3 for demonstration of modern UI
                val initialProgress = items.associate { it.id to 3 }
                _episodeProgress.value = initialProgress
            }
        }
    }

    fun markEpisodeWatched(mediaItemId: Int, season: Int, currentEpisode: Int) {
        viewModelScope.launch {
            repository.logEpisodeWatch(mediaItemId, season, currentEpisode)
            
            // Increment the local UI state
            _episodeProgress.value = _episodeProgress.value.toMutableMap().apply {
                this[mediaItemId] = currentEpisode + 1
            }
        }
    }
}
