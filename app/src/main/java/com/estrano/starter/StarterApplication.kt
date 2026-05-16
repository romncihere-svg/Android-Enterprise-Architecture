package com.estrano.starter

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import com.estrano.starter.BuildConfig

@HiltAndroidApp
class StarterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
