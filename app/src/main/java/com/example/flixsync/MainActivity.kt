package com.example.flixsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flixsync.presentation.home.HomeScreen
import com.example.flixsync.presentation.settings.SettingsScreen
import com.example.flixsync.ui.theme.FlixSyncTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.flixsync.data.repository.ThemeRepository
import com.example.flixsync.domain.model.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themeRepository: ThemeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appTheme by themeRepository.themeFlow.collectAsState(initial = AppTheme.SYSTEM)
            FlixSyncTheme(appTheme = appTheme) {
                FlixSyncApp()
            }
        }
    }
}


@Composable
fun FlixSyncApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
