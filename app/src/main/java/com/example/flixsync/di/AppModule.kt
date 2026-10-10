package com.example.flixsync.di

import android.content.Context
import androidx.room.Room
import com.example.flixsync.data.local.AppDatabase
import com.example.flixsync.data.local.dao.MediaDao
import com.example.flixsync.data.repository.BackupRepositoryImpl
import com.example.flixsync.domain.repository.BackupRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.flixsync.data.remote.TmdbApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "flixsync_db"
        ).addMigrations(AppDatabase.MIGRATION_1_2).build()
    }

    @Provides
    @Singleton
    fun provideMediaDao(database: AppDatabase): MediaDao {
        return database.mediaDao
    }



    @Provides
    @Singleton
    fun provideTmdbApiKeyInterceptor(
        tmdbApiKeyRepository: com.example.flixsync.data.repository.TmdbApiKeyRepository
    ): com.example.flixsync.data.remote.TmdbApiKeyInterceptor {
        return com.example.flixsync.data.remote.TmdbApiKeyInterceptor(tmdbApiKeyRepository)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tmdbApiKeyInterceptor: com.example.flixsync.data.remote.TmdbApiKeyInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(tmdbApiKeyInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideTmdbApiService(okHttpClient: OkHttpClient): TmdbApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBackupRepository(
        @ApplicationContext context: Context,
        mediaDao: MediaDao
    ): BackupRepository {
        return BackupRepositoryImpl(context, mediaDao)
    }
}
