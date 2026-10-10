package com.example.flixsync.domain.repository

import android.net.Uri

interface BackupRepository {
    suspend fun createBackup(outputUri: Uri): Result<Unit>
    suspend fun restoreBackup(inputUri: Uri): Result<Unit>
    suspend fun createAutomatedBackup(): Result<Unit>
}
