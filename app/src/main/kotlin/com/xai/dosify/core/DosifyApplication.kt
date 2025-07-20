package com.xai.dosify.core

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class DosifyApplication : Application() {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        if (Locale.getDefault().country.equals("CN", ignoreCase = true)) {
            // Disable Firebase (e.g., no init or mock)
        }
        // Initialize WorkManager in onCreate
        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        )
    }
}