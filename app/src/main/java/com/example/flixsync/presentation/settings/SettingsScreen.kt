package com.example.flixsync.presentation.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Palette
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val savedApiKey by viewModel.tmdbApiKey.collectAsState()
    val context = LocalContext.current

    var apiKeyInput by rememberSaveable(savedApiKey) { mutableStateOf(savedApiKey) }
    var isKeyVisible by rememberSaveable { mutableStateOf(false) }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
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

    val currentTheme by viewModel.currentTheme.collectAsState()
    var showAppearanceDialog by rememberSaveable { mutableStateOf(false) }

    if (showAppearanceDialog) {
        AlertDialog(
            onDismissRequest = { showAppearanceDialog = false },
            title = {
                Text(
                    text = "Appearance",
                    color = Color.White,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    com.example.flixsync.domain.model.AppTheme.entries.forEach { theme ->
                        val isSelected = theme == currentTheme
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF262626) else Color.Transparent)
                                .clickable {
                                    viewModel.selectTheme(theme)
                                    showAppearanceDialog = false
                                }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = theme.displayName,
                                color = if (isSelected) Color(0xFFE8FF47) else Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    viewModel.selectTheme(theme)
                                    showAppearanceDialog = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFFE8FF47),
                                    unselectedColor = Color(0xFF888888)
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAppearanceDialog = false }) {
                    Text("Close", color = Color(0xFFE8FF47))
                }
            },
            containerColor = Color(0xFF1E1E1E),
            shape = RoundedCornerShape(24.dp)
        )
    }

    Scaffold(
        containerColor = Color(0xFF0D0D0D),
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D0D0D),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D0D0D))
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState is SettingsUiState.Loading) {
                item {
                    CircularProgressIndicator(color = Color(0xFFE8FF47))
                }
            }

            // Appearance Setting Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { showAppearanceDialog = true },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF171717)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF242424)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Palette,
                                    contentDescription = "Appearance",
                                    tint = Color(0xFF4AC4F3)
                                )
                            }
                            Column {
                                Text(
                                    text = "Appearance",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = "Theme mode",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF888888)
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = currentTheme.displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF888888)
                            )
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color(0xFF666666)
                            )
                        }
                    }
                }
            }

            // Backup & Restore Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF171717)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Backup & Restore (.flixbk)",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Export your watched list, episode logs, and library to a .flixbk backup file, or restore an existing one.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9A9A9A)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US).format(java.util.Date())
                                    createDocumentLauncher.launch("flixsync_backup_$timestamp.flixbk")
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF242424),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Create Backup")
                            }

                            OutlinedButton(
                                onClick = {
                                    openDocumentLauncher.launch(arrayOf("application/octet-stream", "application/json", "*/*"))
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Restore Backup")
                            }
                        }
                    }
                }
            }

            // TMDb API Key Management Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF171717)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "TMDb API Key",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Enter a custom TMDb v3 API key for network requests. If empty, the app falls back to the default configuration key.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9A9A9A)
                        )
                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = { apiKeyInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter TMDb API key", color = Color(0xFF666666)) },
                            singleLine = true,
                            visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                TextButton(onClick = { isKeyVisible = !isKeyVisible }) {
                                    Text(
                                        text = if (isKeyVisible) "HIDE" else "SHOW",
                                        color = Color(0xFF9A9A9A),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFE8FF47),
                                unfocusedBorderColor = Color(0xFF444444),
                                cursorColor = Color(0xFFE8FF47)
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    viewModel.saveApiKey(apiKeyInput.trim())
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE8FF47),
                                    contentColor = Color.Black
                                )
                            ) {
                                Text("Save Key")
                            }
                        }
                    }
                }
            }
        }
    }
}
