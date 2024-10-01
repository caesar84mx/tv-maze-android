package com.caesar84mx.tvmaze.di

import com.caesar84mx.tvmaze.BuildConfig
import com.caesar84mx.tvmaze.data.networking.TvMazeService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

private fun provideHttpClient(): OkHttpClient {
    return OkHttpClient
        .Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()
}

private fun provideRetrofit(
    okHttpClient: OkHttpClient,
): Retrofit {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    return Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
        .client(okHttpClient)
        .build()
}

private fun provideTvMazeApiService(retrofit: Retrofit): TvMazeService {
    return retrofit.create(TvMazeService::class.java)
}

val networkModule = module {
    single { provideHttpClient() }
    single { provideRetrofit(get()) }
    single { provideTvMazeApiService(get()) }
}