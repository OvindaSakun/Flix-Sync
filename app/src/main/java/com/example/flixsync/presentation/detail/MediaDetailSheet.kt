package com.example.flixsync.presentation.detail
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.flixsync.domain.model.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun MediaDetailSheet(item:MediaItem,tracked:TrackedMedia?,dismiss:()->Unit,add:()->Unit,remove:(Boolean)->Unit,movie:(Boolean)->Unit,episode:(Int,Int,Boolean)->Unit){ModalBottomSheet(onDismissRequest=dismiss){LazyColumn(contentPadding=PaddingValues(bottom=48.dp)){item{Box{AsyncImage(item.backdropUrl.ifBlank{item.posterUrl},null,Modifier.fillMaxWidth().height(230.dp),contentScale=ContentScale.Crop);IconButton(dismiss){Icon(Icons.Outlined.Close,"Close")}}};item{Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Text(item.title,style=MaterialTheme.typography.headlineMedium);Text(listOf(if(item.mediaType==MediaType.MOVIE)"Movie" else "TV",item.releaseDate.take(4),item.runtimeMinutes?.let{"$it min"}).filterNotNull().joinToString(" · "),color=MaterialTheme.colorScheme.onSurfaceVariant);Text(item.overview.ifBlank{"No synopsis available."});if(tracked==null)Button(onClick=add,modifier=Modifier.fillMaxWidth()){Text("Add to library")}else{if(item.mediaType==MediaType.MOVIE){val w=(0 to 0) in tracked.watched;Button(onClick={movie(!w)},modifier=Modifier.fillMaxWidth()){Text(if(w)"Mark unwatched" else "Mark watched")}};OutlinedButton(onClick={remove(false)},modifier=Modifier.fillMaxWidth()){Text("Remove (keep history)")};TextButton(onClick={remove(true)},modifier=Modifier.fillMaxWidth()){Text("Remove and delete history")}}}};if(tracked!=null&&item.mediaType==MediaType.TV){if(tracked.episodes.isEmpty())item{Text("Episode metadata unavailable. Connect to the internet and re-add to retry.",Modifier.padding(20.dp))}else items(tracked.episodes.sortedWith(compareBy({it.seasonNumber},{it.episodeNumber}))){ep->val w=(ep.seasonNumber to ep.episodeNumber) in tracked.watched;ListItem(headlineContent={Text("S%02d E%02d  %s".format(ep.seasonNumber,ep.episodeNumber,ep.name))},trailingContent={Checkbox(w,{episode(ep.seasonNumber,ep.episodeNumber,it)})})}}}}
}
