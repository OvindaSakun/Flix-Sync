package com.example.flixsync.presentation.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.flixsync.domain.model.*
import com.example.flixsync.presentation.detail.MediaDetailSheet

private val Accent=Color(0xFFFF725E)
enum class MainTab(val label:String){TV("TV Shows"),MOVIES("Movies"),SEARCH("Search"),PROFILE("Profile")}
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HomeScreen(onNavigateToSettings:()->Unit,vm:HomeViewModel=hiltViewModel()){
 var tab by rememberSaveable{mutableStateOf(MainTab.TV)};val library by vm.library.collectAsStateWithLifecycle();val search by vm.search.collectAsStateWithLifecycle();val selected by vm.selected.collectAsStateWithLifecycle();val msg by vm.message.collectAsStateWithLifecycle();val movies by vm.watchedMovies.collectAsStateWithLifecycle();val episodes by vm.watchedEpisodes.collectAsStateWithLifecycle()
 Scaffold(containerColor=MaterialTheme.colorScheme.background,bottomBar={FloatingNav(tab){tab=it}}){pad->Box(Modifier.fillMaxSize().padding(bottom=pad.calculateBottomPadding())){when(tab){
  MainTab.TV->LibraryList("Watch next",library.filter{it.media.mediaType==MediaType.TV},vm::select,vm::next)
  MainTab.MOVIES->LibraryList("Movies",library.filter{it.media.mediaType==MediaType.MOVIE},vm::select){vm.movie(it.media,(0 to 0)!in it.watched)}
  MainTab.SEARCH->SearchPage(search,vm)
  MainTab.PROFILE->ProfilePage(movies,episodes,onNavigateToSettings)
 }}}
 selected?.let{item->val tracked=library.firstOrNull{it.media.key==item.key};MediaDetailSheet(item,tracked,{vm.closeDetails()},{vm.add(item)},{purge->vm.remove(item,purge)},{watched->vm.movie(item,watched)},{s,e,w->vm.episode(item,s,e,w)})}
 msg?.let{LaunchedEffect(it){vm.clearMessage()};Snackbar(modifier=Modifier.padding(16.dp)){Text(it)}}
}
@Composable private fun LibraryList(title:String,items:List<TrackedMedia>,open:(MediaItem)->Unit,quick:(TrackedMedia)->Unit){LazyColumn(contentPadding=PaddingValues(20.dp,28.dp,20.dp,110.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){item{Text(title,style=MaterialTheme.typography.headlineLarge,fontWeight=FontWeight.Bold)};if(items.isEmpty())item{Empty("Nothing here yet","Add titles from Search to build your library.")};items(items,key={it.media.key}){t->LibraryRow(t,{open(t.media)},{quick(t)})}}}
@Composable private fun LibraryRow(t:TrackedMedia,open:()->Unit,quick:()->Unit){Surface(onClick=open,shape=RoundedCornerShape(22.dp),color=MaterialTheme.colorScheme.surface,modifier=Modifier.fillMaxWidth()){Row(Modifier.height(142.dp),verticalAlignment=Alignment.CenterVertically){AsyncImage(t.media.posterUrl,null,Modifier.width(96.dp).fillMaxHeight(),contentScale=ContentScale.Crop);Column(Modifier.weight(1f).padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){Text(t.media.title,maxLines=1,overflow=TextOverflow.Ellipsis,style=MaterialTheme.typography.titleMedium);if(t.media.mediaType==MediaType.TV){val n=t.nextEpisode;Text(if(t.complete)"Complete · ${t.watchedCount}/${t.episodes.size}" else n?.let{"S%02d E%02d · ${t.watchedCount}/${t.episodes.size}".format(it.seasonNumber,it.episodeNumber)}?:"Episode data unavailable",color=MaterialTheme.colorScheme.onSurfaceVariant);n?.name?.takeIf{it.isNotBlank()}?.let{Text(it,maxLines=1,overflow=TextOverflow.Ellipsis,color=MaterialTheme.colorScheme.onSurfaceVariant)}}else Text(if((0 to 0) in t.watched)"Watched" else t.media.releaseDate.take(4),color=MaterialTheme.colorScheme.onSurfaceVariant)};IconButton(onClick=quick,enabled=t.media.mediaType==MediaType.MOVIE||t.nextEpisode!=null){Icon(if(t.media.mediaType==MediaType.MOVIE&&(0 to 0) in t.watched)Icons.Outlined.CheckCircle else Icons.Outlined.Check,"Mark watched",tint=Accent)}}}}
@Composable private fun SearchPage(s:SearchState,vm:HomeViewModel){LazyColumn(contentPadding=PaddingValues(20.dp,24.dp,20.dp,110.dp),verticalArrangement=Arrangement.spacedBy(18.dp)){item{OutlinedTextField(s.query,vm::setQuery,Modifier.fillMaxWidth(),placeholder={Text("Search movies and TV shows")},leadingIcon={Icon(Icons.Outlined.Search,null)},shape=RoundedCornerShape(28.dp),singleLine=true)};item{LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){items(SearchFilter.entries){f->FilterChip(selected=s.filter==f,onClick={vm.setFilter(f)},label={Text(when(f){SearchFilter.TV->"TV";else->f.name.lowercase().replaceFirstChar{it.uppercase()}})})}}};when{ s.loading->item{Box(Modifier.fillParentMaxWidth().padding(60.dp),contentAlignment=Alignment.Center){CircularProgressIndicator(color=Accent)}};s.error!=null->item{Column(horizontalAlignment=Alignment.CenterHorizontally,modifier=Modifier.fillParentMaxWidth()){Text(s.error);TextButton(onClick=vm::retry){Text("Retry")}}};s.items.isEmpty()->item{Empty("No results","Try another title or filter.")};else->items(s.items,key={it.key}){PosterRow(it){vm.select(it)}}}}
}
@Composable private fun PosterRow(item:MediaItem,open:()->Unit){Row(Modifier.fillMaxWidth().clickable(onClick=open),verticalAlignment=Alignment.CenterVertically){AsyncImage(item.posterUrl,null,Modifier.size(82.dp,122.dp).clip(RoundedCornerShape(16.dp)),contentScale=ContentScale.Crop);Column(Modifier.padding(16.dp).weight(1f)){Text(item.title,style=MaterialTheme.typography.titleLarge,maxLines=2);Text("${if(item.mediaType==MediaType.MOVIE)"Movie" else if(item.isAnime)"Anime · TV" else "TV"} · ${item.releaseDate.take(4)}",color=MaterialTheme.colorScheme.onSurfaceVariant);Text(item.overview,maxLines=2,overflow=TextOverflow.Ellipsis,color=MaterialTheme.colorScheme.onSurfaceVariant)}}
}
@Composable private fun ProfilePage(movies:Int,episodes:Int,settings:()->Unit){Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),verticalArrangement=Arrangement.spacedBy(22.dp)){Text("Profile",style=MaterialTheme.typography.headlineLarge,fontWeight=FontWeight.Bold);Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){Stat("MOVIES",movies,Modifier.weight(1f));Stat("EPISODES",episodes,Modifier.weight(1f))};Surface(onClick=settings,shape=RoundedCornerShape(20.dp),color=MaterialTheme.colorScheme.surface){Row(Modifier.fillMaxWidth().padding(20.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Outlined.Settings,null,tint=Accent);Text("Settings & backup",Modifier.padding(start=16.dp).weight(1f));Icon(Icons.Outlined.ChevronRight,null)}}}}
@Composable private fun Stat(label:String,value:Int,m:Modifier){Surface(m,shape=RoundedCornerShape(20.dp),color=MaterialTheme.colorScheme.surface){Column(Modifier.padding(20.dp)){Text(label,color=MaterialTheme.colorScheme.onSurfaceVariant);Text(value.toString(),style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)}}}
@Composable private fun Empty(title:String,body:String){Column(Modifier.fillMaxWidth().padding(vertical=72.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(title,style=MaterialTheme.typography.titleLarge);Text(body,color=MaterialTheme.colorScheme.onSurfaceVariant)}}
@Composable private fun FloatingNav(tab:MainTab,onTab:(MainTab)->Unit){Surface(Modifier.navigationBarsPadding().padding(18.dp).fillMaxWidth(),shape=RoundedCornerShape(34.dp),color=MaterialTheme.colorScheme.surface.copy(.96f),shadowElevation=14.dp){Row(Modifier.height(68.dp),horizontalArrangement=Arrangement.SpaceEvenly,verticalAlignment=Alignment.CenterVertically){MainTab.entries.forEach{t->val icon=when(t){MainTab.TV->Icons.Outlined.Tv;MainTab.MOVIES->Icons.Outlined.Movie;MainTab.SEARCH->Icons.Outlined.Search;MainTab.PROFILE->Icons.Outlined.AccountCircle};IconButton(onClick={onTab(t)},modifier=Modifier.weight(1f)){Icon(icon,t.label,tint=if(t==tab)Accent else MaterialTheme.colorScheme.onSurfaceVariant)}}}}}
