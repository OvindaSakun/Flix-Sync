package com.example.flixsync.data.repository

import android.content.Context
import android.net.Uri
import com.example.flixsync.data.local.dao.MediaDao
import com.example.flixsync.data.local.entity.EpisodeWatchLogEntity
import com.example.flixsync.domain.model.BackupData
import com.example.flixsync.domain.repository.BackupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    private val context: Context,
    private val mediaDao: MediaDao
) : BackupRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    override suspend fun createBackup(outputUri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val backupData = getBackupData()
            val jsonString = json.encodeToString(backupData)

            context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                outputStream.write(jsonString.toByteArray())
            } ?: return@withContext Result.failure(Exception("Failed to open output stream"))

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun restoreBackup(inputUri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.contentResolver.openInputStream(inputUri)?.use { inputStream ->
                inputStream.bufferedReader().use { it.readText() }
            } ?: return@withContext Result.failure(Exception("Failed to open input stream"))

            val backupData = json.decodeFromString<BackupData>(jsonString)
            restoreData(backupData)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createAutomatedBackup(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val backupData = getBackupData()
            val jsonString = json.encodeToString(backupData)

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val filename = "flixsync_backup_$timestamp.flixbk"
            
            // Save to external files dir (Documents or similar) or internal files dir
            val backupDir = File(context.getExternalFilesDir(null), "Backups")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }
            
            val file = File(backupDir, filename)
            file.writeText(jsonString)

            // Keep only the last 5 automated backups
            val backups = backupDir.listFiles()?.filter { it.name.endsWith(".flixbk") || it.name.endsWith(".traxbk") }?.sortedBy { it.lastModified() }
            if (backups != null && backups.size > 5) {
                backups.take(backups.size - 5).forEach { it.delete() }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getBackupData(): BackupData {
        val mediaEntities = mediaDao.getAllMediaList()
        val watchLogEntities = mediaDao.getAllWatchLogsList()

        return BackupData(
            timestamp = System.currentTimeMillis(),
            mediaItems = mediaEntities.map { it.toDomain() },
            watchLogs = watchLogEntities.map { it.toDomain() },
            episodes = mediaDao.getAllEpisodesList().map { it.toDomain() }
        )
    }

    private suspend fun restoreData(backupData: BackupData) {
        val loggedIds = backupData.watchLogs.map { it.mediaItemId }.toSet()
        val loggedKeys = backupData.watchLogs.map { it.mediaKey }.filter { it.isNotBlank() }.toSet()
        val mediaEntities = backupData.mediaItems.map { item ->
            com.example.flixsync.data.local.entity.MediaEntity.fromDomain(item, item.inLibrary || item.id in loggedIds || item.key in loggedKeys)
        }
        val known = mediaEntities.associateBy { it.mediaKey }
        val watchLogEntities = backupData.watchLogs.mapNotNull { log ->
            val key = log.mediaKey.ifBlank {
                val matches = mediaEntities.filter { it.tmdbId == log.mediaItemId }
                when { matches.size == 1 -> matches.single().mediaKey; else -> "${log.mediaType.name.lowercase()}:${log.mediaItemId}" }
            }
            if (known[key] == null) null else EpisodeWatchLogEntity(key, log.seasonNumber, log.episodeNumber, log.watchedAt)
        }
        val episodeEntities = backupData.episodes.filter { known[it.mediaKey] != null }.map { com.example.flixsync.data.local.entity.EpisodeEntity(it.mediaKey,it.seasonNumber,it.episodeNumber,it.name,it.runtimeMinutes) }
        mediaDao.restore(mediaEntities, episodeEntities, watchLogEntities)
    }
}
