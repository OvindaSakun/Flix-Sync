package com.example.flixsync.data.remote

import com.example.flixsync.BuildConfig
import com.example.flixsync.data.repository.TmdbApiKeyRepository
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TmdbApiKeyInterceptor @Inject constructor(
    private val tmdbApiKeyRepository: TmdbApiKeyRepository,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalHttpUrl = originalRequest.url

        val customKey = tmdbApiKeyRepository.getApiKey()
        val apiKey = customKey.ifBlank {
            val defaultKey = BuildConfig.TMDB_API_KEY
            if (defaultKey.isNotBlank() && defaultKey != "null" && defaultKey != "STUB_KEY") {
                defaultKey
            } else {
                "f0e08940a173a9db73b437a54b65b3ec"
            }
        }

        val newHttpUrl = originalHttpUrl
            .newBuilder()
            .addQueryParameter("api_key", apiKey)
            .build()

        val requestBuilder = originalRequest
            .newBuilder()
            .url(newHttpUrl)

        val newRequest = requestBuilder.build()
        return chain.proceed(newRequest)
    }
}
