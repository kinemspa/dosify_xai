package com.xai.dosify.core

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class DosifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Locale.getDefault().country.equals("CN", ignoreCase = true)) {
            // Disable Firebase (e.g., no init or mock)
        }
    }
}