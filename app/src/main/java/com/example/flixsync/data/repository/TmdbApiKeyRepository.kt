package com.example.flixsync.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tmdbApiKeyDataStore by preferencesDataStore(name = "tmdb_api_key_preferences")

@Singleton
class TmdbApiKeyRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val apiKeyKey = stringPreferencesKey("tmdb_custom_api_key")
    private val cachedKey = AtomicReference("")

    init {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            context.tmdbApiKeyDataStore.data
                .map { preferences -> preferences[apiKeyKey]?.trim().orEmpty() }
                .collect { key -> cachedKey.set(key) }
        }
    }

    val apiKeyFlow: Flow<String> = context.tmdbApiKeyDataStore.data.map { preferences ->
        preferences[apiKeyKey]?.trim().orEmpty()
    }

    fun getApiKey(): String {
        val cached = cachedKey.get()
        if (cached.isNotBlank()) return cached
        return runCatching {
            runBlocking(Dispatchers.IO) {
                val key = context.tmdbApiKeyDataStore.data.first()[apiKeyKey]?.trim().orEmpty()
                cachedKey.set(key)
                key
            }
        }.getOrDefault("")
    }

    suspend fun setApiKey(key: String) {
        val trimmed = key.trim()
        cachedKey.set(trimmed)
        context.tmdbApiKeyDataStore.edit { preferences ->
            preferences[apiKeyKey] = trimmed
        }
    }

    suspend fun clearApiKey() {
        cachedKey.set("")
        context.tmdbApiKeyDataStore.edit { preferences ->
            preferences.remove(apiKeyKey)
        }
    }
}
