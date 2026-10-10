package com.example.flixsync.presentation.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flixsync.data.repository.TmdbApiKeyRepository
import com.example.flixsync.data.repository.ThemeRepository
import com.example.flixsync.domain.model.AppTheme
import com.example.flixsync.domain.repository.BackupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    private val tmdbApiKeyRepository: TmdbApiKeyRepository,
    private val themeRepository: ThemeRepository,
) : ViewModel() {

    val currentTheme: StateFlow<AppTheme> = themeRepository.themeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.SYSTEM)

    val tmdbApiKey: StateFlow<String> = tmdbApiKeyRepository.apiKeyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), tmdbApiKeyRepository.getApiKey())

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun createBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loading
            val result = backupRepository.createBackup(uri)
            if (result.isSuccess) {
                _uiState.value = SettingsUiState.Success("Backup created successfully")
            } else {
                _uiState.value = SettingsUiState.Error(result.exceptionOrNull()?.message ?: "Failed to create backup")
            }
        }
    }

    fun restoreBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loading
            val result = backupRepository.restoreBackup(uri)
            if (result.isSuccess) {
                _uiState.value = SettingsUiState.Success("Backup restored successfully")
            } else {
                _uiState.value = SettingsUiState.Error(result.exceptionOrNull()?.message ?: "Failed to restore backup")
            }
        }
    }

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            tmdbApiKeyRepository.setApiKey(key)
            _uiState.value = SettingsUiState.Success("API Key saved successfully")
        }
    }

    fun selectTheme(theme: AppTheme) {
        viewModelScope.launch {
            themeRepository.setTheme(theme)
        }
    }

    fun resetState() {
        _uiState.value = SettingsUiState.Idle
    }
}

sealed class SettingsUiState {
    object Idle : SettingsUiState()
    object Loading : SettingsUiState()
    data class Success(val message: String) : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
}
