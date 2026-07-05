package com.example.mymoviedb.presentation.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymoviedb.domain.repository.BackupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val backupRepository: BackupRepository
) : ViewModel() {

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
