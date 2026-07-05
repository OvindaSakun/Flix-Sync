package com.example.mymoviedb.di

import android.content.Context
import androidx.room.Room
import com.example.mymoviedb.data.local.AppDatabase
import com.example.mymoviedb.data.local.dao.MediaDao
import com.example.mymoviedb.data.repository.BackupRepositoryImpl
import com.example.mymoviedb.domain.repository.BackupRepository
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
import com.example.mymoviedb.data.remote.TmdbApiService
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
            "mymoviedb_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideMediaDao(database: AppDatabase): MediaDao {
        return database.mediaDao
    }



    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val originalUrl = originalRequest.url
            val newUrl = originalUrl.newBuilder()
                .addQueryParameter("api_key", "f0e08940a173a9db73b437a54b65b3ec")
                .build()
            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()
            chain.proceed(newRequest)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
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