package com.example.flixsync.domain.model

enum class AppTheme(val storageKey: String, val displayName: String) {
    SYSTEM("system", "System default"),
    DARK("dark", "Dark"),
    LIGHT("light", "Light");

    companion object {
        fun fromStorageKey(key: String?): AppTheme {
            return entries.firstOrNull { it.storageKey.equals(key, ignoreCase = true) } ?: SYSTEM
        }
    }
}
