package com.example.mymoviedb.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mymoviedb.domain.repository.BackupRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val backupRepository: BackupRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val result = backupRepository.createAutomatedBackup()
            if (result.isSuccess) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
