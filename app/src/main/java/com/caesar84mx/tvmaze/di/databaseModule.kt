package com.caesar84mx.tvmaze.di

import android.app.Application
import androidx.room.Room
import com.caesar84mx.tvmaze.BuildConfig
import com.caesar84mx.tvmaze.data.dao.ShowDao
import com.caesar84mx.tvmaze.data.dao.TvMazeDatabase
import com.caesar84mx.tvmaze.data.dao.EpisodeDao
import org.koin.dsl.module

private fun provideDataBase(application: Application): TvMazeDatabase =
    Room.databaseBuilder(
        application,
        TvMazeDatabase::class.java,
        BuildConfig.DATABASE_NAME
    )
        .fallbackToDestructiveMigration().build()

private fun provideShowsDao(dataBase: TvMazeDatabase): ShowDao = dataBase.showDao()

private fun provideEpisodesDao(dataBase: TvMazeDatabase): EpisodeDao = dataBase.episodeDao()


val dataBaseModule= module {
    single { provideDataBase(get()) }
    single { provideShowsDao(get()) }
    single { provideEpisodesDao(get()) }
}