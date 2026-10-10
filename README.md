# Flix-Sync

<p align="center">
  <strong>Offline-first media & episode tracker for Movies, TV Series, and Anime.</strong>
</p>

<p align="center">
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white">
  <img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white">
  <img alt="Room" src="https://img.shields.io/badge/Room-Reactive%20SSOT-34A853?logo=sqlite&logoColor=white">
  <img alt="Dagger Hilt" src="https://img.shields.io/badge/DI-Dagger%20Hilt-E8FF47?logo=dagger&logoColor=111111">
  <img alt="Min SDK" src="https://img.shields.io/badge/Min%20SDK-30-3DDC84?logo=android&logoColor=white">
  <img alt="Target SDK" src="https://img.shields.io/badge/Target%20SDK-36-3DDC84?logo=android&logoColor=white">
  <img alt="License" src="https://img.shields.io/badge/License-MIT-blue.svg">
</p>

---

## 📸 Screenshots

| Watch Next | Movies | Search | Profile | Settings |
|:---:|:---:|:---:|:---:|:---:|
| <img src="docs/screenshots/01_watch_next.jpg" width="180"/> | <img src="docs/screenshots/02_movies.jpg" width="180"/> | <img src="docs/screenshots/03_search.jpg" width="180"/> | <img src="docs/screenshots/04_profile.jpg" width="180"/> | <img src="docs/screenshots/05_settings.jpg" width="180"/> |

---

## ⚡ Features

- **Reactive Single Source of Truth**: Room SQLite drives all UI states reactively via Kotlin Flows—no fragile in-memory mirrors or dropped watch states.
- **Watch Next Feed**: Calculates the exact next unwatched episode across seasons (`S01 E05 · 4/41`) with single-tap quick progression.
- **Namespaced Identifiers**: Distinguishes overlapping TMDb numeric IDs (`movie:42` vs `tv:42`) to prevent state collisions.
- **TMDb Multi-Search**: Debounced live searching with category chips (`All`, `Movies`, `TV`, `Anime`).
- **Real SQLite Statistics**: Watch counts and episode logs on the profile screen derive directly from database row counts.
- **Safe Backups & Migration**: Transactional `.flixbk` backup import/export, 24h rolling local backups via WorkManager, and non-destructive Room v1 -> v2 migration.

---

## 🛠️ Tech Stack

- **UI**: Jetpack Compose & Material 3 (with custom dark/light theme support)
- **Database**: Room (SQLite)
- **Architecture**: MVVM / Clean Architecture with reactive Kotlin Coroutines & Flow
- **Dependency Injection**: Dagger Hilt
- **Network**: Retrofit & OkHttp (TMDb API v3) + Coil image loading
- **Background Tasks & Preferences**: WorkManager & Jetpack DataStore

---

## 🚀 Getting Started

### 1. Prerequisites
- Android Studio Ladybug (or newer)
- JDK 17 / 21
- Android SDK Platform 36 (Minimum API 30)

### 2. Configure TMDb API Key
Add your TMDb API key to `local.properties` in the project root:

```properties
sdk.dir=C\:\\Users\\YourUser\\AppData\\Local\\Android\\Sdk
TMDB_API_KEY="your_tmdb_v3_api_key"
```

*(You can also configure or change the API key directly within the app settings screen.)*

### 3. Build & Install

```powershell
# Build Debug APK
.\gradlew.bat assembleDebug

# Run Unit Tests
.\gradlew.bat testDebugUnitTest

# Install to connected device
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📄 License

This project is licensed under the MIT License.
