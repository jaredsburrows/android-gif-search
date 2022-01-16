package com.burrowsapps.example.gif

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.setCompatVectorFromResourcesEnabled
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class App : Application() {
  override fun onCreate() {
    super.onCreate()
    // Force night mode for this application, we want it to look the same on all devices
    setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
    setCompatVectorFromResourcesEnabled(true)
  }
}
