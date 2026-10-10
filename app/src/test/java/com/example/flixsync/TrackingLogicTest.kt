package com.example.flixsync
import com.example.flixsync.domain.model.*
import org.junit.Assert.*
import org.junit.Test
class TrackingLogicTest {
 private val show=MediaItem(42,"Show",mediaType=MediaType.TV)
 private val episodes=listOf(Episode(show.key,1,1),Episode(show.key,1,2),Episode(show.key,2,1))
 @Test fun gaps_are_next_before_higher_watched_episodes(){val t=TrackedMedia(show,episodes,setOf(1 to 1,2 to 1));assertEquals(1,t.nextEpisode?.seasonNumber);assertEquals(2,t.nextEpisode?.episodeNumber)}
 @Test fun season_transition_uses_ordered_identity(){val t=TrackedMedia(show,episodes,setOf(1 to 1,1 to 2));assertEquals(2 to 1,t.nextEpisode?.let{it.seasonNumber to it.episodeNumber})}
 @Test fun movie_and_tv_ids_have_distinct_namespaces(){assertNotEquals(MediaItem(7,"M",mediaType=MediaType.MOVIE).key,MediaItem(7,"T",mediaType=MediaType.TV).key)}
}
