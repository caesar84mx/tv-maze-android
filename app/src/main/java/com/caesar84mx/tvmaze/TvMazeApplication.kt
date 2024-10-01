package com.caesar84mx.tvmaze

import android.app.Application
import com.caesar84mx.tvmaze.di.dataBaseModule
import com.caesar84mx.tvmaze.di.networkModule
import com.caesar84mx.tvmaze.di.coreElementsModule
import com.caesar84mx.tvmaze.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class TvMazeApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@TvMazeApplication)
            modules(networkModule, dataBaseModule, coreElementsModule, viewModelsModule)
        }
    }
}