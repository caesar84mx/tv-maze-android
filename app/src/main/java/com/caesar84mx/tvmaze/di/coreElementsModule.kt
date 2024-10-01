package com.caesar84mx.tvmaze.di

import android.content.Context
import android.content.SharedPreferences
import com.caesar84mx.tvmaze.BuildConfig
import com.caesar84mx.tvmaze.data.repository.EpisodesRepository
import com.caesar84mx.tvmaze.data.repository.EpisodesRepositoryImpl
import com.caesar84mx.tvmaze.data.repository.ShowsRepository
import com.caesar84mx.tvmaze.data.repository.ShowsRepositoryImpl
import com.caesar84mx.tvmaze.util.navigation.Navigator
import com.caesar84mx.tvmaze.util.navigation.TvMazeNavigator
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val coreElementsModule = module {
    single<SharedPreferences> {
        androidApplication().getSharedPreferences(
            BuildConfig.SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }
    single<Navigator> { TvMazeNavigator() }
    single<ShowsRepository> { ShowsRepositoryImpl(get(), get(), get()) }
    single<EpisodesRepository> { EpisodesRepositoryImpl(get(), get(), get()) }
}