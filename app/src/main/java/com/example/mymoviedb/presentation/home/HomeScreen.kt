package com.example.mymoviedb.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mymoviedb.domain.model.MediaItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val trendingMedia by viewModel.trendingMedia.collectAsState()
    val episodeProgress by viewModel.episodeProgress.collectAsState()
    
    var selectedTabIndex by remember { mutableStateOf(0) }
    var selectedBottomNavIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = { Text("FlixTrax") },
                    actions = {
                        IconButton(onClick = { /* Toggle View */ }) {
                            Icon(imageVector = Icons.Default.List, contentDescription = "Toggle View")
                        }
                    }
                )
                PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("TO WATCH") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("UPCOMING") }
                    )
                }
                Box(modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)) {
                    FilterChip(
                        selected = false,
                        onClick = { /* Filter */ },
                        label = { Text("WATCH LATER") },
                        trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) }
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(if (selectedBottomNavIndex == 0) Icons.Filled.Tv else Icons.Outlined.Tv, contentDescription = "Watchlist") },
                    selected = selectedBottomNavIndex == 0,
                    onClick = { selectedBottomNavIndex = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(if (selectedBottomNavIndex == 1) Icons.Filled.Movie else Icons.Outlined.Movie, contentDescription = "Library") },
                    selected = selectedBottomNavIndex == 1,
                    onClick = { selectedBottomNavIndex = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.AutoAwesome, contentDescription = "Discover") },
                    selected = selectedBottomNavIndex == 2,
                    onClick = { selectedBottomNavIndex = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Search, contentDescription = "Search") },
                    selected = selectedBottomNavIndex == 3,
                    onClick = { selectedBottomNavIndex = 3 }
                )
                NavigationBarItem(
                    icon = { Icon(if (selectedBottomNavIndex == 4) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Profile") },
                    selected = selectedBottomNavIndex == 4,
                    onClick = { selectedBottomNavIndex = 4; onNavigateToSettings() }
                )
            }
        }
    ) { padding ->
        if (trendingMedia.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(trendingMedia) { item ->
                    val currentEpisode = episodeProgress[item.id] ?: 1
                    MediaItemCard(
                        item = item,
                        currentEpisode = currentEpisode,
                        onMarkWatched = { viewModel.markEpisodeWatched(item.id, season = 1, currentEpisode = currentEpisode) }
                    )
                }
            }
        }
    }
}

@Composable
fun MediaItemCard(
    item: MediaItem,
    currentEpisode: Int,
    onMarkWatched: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().height(140.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left: Poster
            AsyncImage(
                model = item.posterUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(100.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Middle: Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = "${item.title.uppercase()} >",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                
                Text(
                    text = "S01 | E${currentEpisode.toString().padStart(2, '0')}  +364",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Episode Title Placeholder",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }
            
            // Right: Checkmark Button
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onMarkWatched,
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(50))
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Mark Watched",
                        tint = MaterialTheme.colorScheme.inverseOnSurface
                    )
                }
            }
        }
    }
}
