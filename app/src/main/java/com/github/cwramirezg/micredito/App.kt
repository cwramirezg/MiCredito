package com.github.cwramirezg.micredito

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        //configurarWorkManager()
    }

    private fun configurarWorkManager() {
        val workManagerConfiguration = Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.ERROR)
            .build()

        WorkManager.initialize(this, workManagerConfiguration)
    }
}
