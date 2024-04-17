package com.burrowsapps.gif.search

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.setCompatVectorFromResourcesEnabled
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import dagger.hilt.android.HiltAndroidApp

/**
 * The main application class for this app.
 *
 * This class is annotated with @HiltAndroidApp, which enables the Hilt dependency injection
 * framework for the entire app. It also extends the Application class, and overrides its onCreate()
 * method to set the default night mode and enable compatibility mode for vector drawables.
 */
@HiltAndroidApp
open class MainApplication : Application() {
  /**
   * Called when the application is first created.
   *
   * This method is called by the Android system when the application is first created. It
   * overrides the default implementation of the onCreate() method in the Application class, and
   * sets the default night mode and enables compatibility mode for vector drawables. Specifically,
   * it sets the default night mode to MODE_NIGHT_FOLLOW_SYSTEM if the device is running Android Q
   * or later, and MODE_NIGHT_AUTO_BATTERY if the device is running an earlier version of Android.
   * It also enables compatibility mode for vector drawables, which allows them to be used on older
   * versions of Android.
   */
  override fun onCreate() {
    super.onCreate()
    setDefaultNightMode(if (SDK_INT >= Q) MODE_NIGHT_FOLLOW_SYSTEM else MODE_NIGHT_AUTO_BATTERY)
    setCompatVectorFromResourcesEnabled(true)
  }
}
