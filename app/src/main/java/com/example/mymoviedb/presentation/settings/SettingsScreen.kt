package com.example.mymoviedb.presentation.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.createBackup(it) }
    }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.restoreBackup(it) }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is SettingsUiState.Success -> {
                Toast.makeText(context, (uiState as SettingsUiState.Success).message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            is SettingsUiState.Error -> {
                Toast.makeText(context, (uiState as SettingsUiState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState is SettingsUiState.Loading) {
                CircularProgressIndicator()
            }

            Button(
                onClick = {
                    val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US).format(java.util.Date())
                    createDocumentLauncher.launch("flixtrax_backup_$timestamp.traxbk")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Backup")
            }

            Button(
                onClick = {
                    openDocumentLauncher.launch(arrayOf("*/*"))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Restore Backup")
            }
        }
    }
}
