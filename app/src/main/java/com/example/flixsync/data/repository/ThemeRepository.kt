package com.example.flixsync.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.flixsync.domain.model.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

private val Context.themePreferencesDataStore by preferencesDataStore(name = "theme_preferences")

@Singleton
class ThemeRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val themeKey = stringPreferencesKey("app_appearance_theme")
    private val cachedTheme = AtomicReference(AppTheme.SYSTEM)

    init {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            context.themePreferencesDataStore.data
                .map { preferences ->
                    AppTheme.fromStorageKey(preferences[themeKey])
                }
                .collect { theme ->
                    cachedTheme.set(theme)
                }
        }
    }

    val themeFlow: Flow<AppTheme> = context.themePreferencesDataStore.data
        .map { preferences ->
            AppTheme.fromStorageKey(preferences[themeKey])
        }
        .distinctUntilChanged()

    suspend fun setTheme(theme: AppTheme) {
        cachedTheme.set(theme)
        context.themePreferencesDataStore.edit { preferences ->
            preferences[themeKey] = theme.storageKey
        }
    }
}
