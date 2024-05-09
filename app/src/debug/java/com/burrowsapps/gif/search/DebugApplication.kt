package com.burrowsapps.gif.search

import android.os.StrictMode
import timber.log.Timber
import timber.log.Timber.DebugTree

class DebugApplication : MainApplication() {
  override fun onCreate() {
    super.onCreate()

    Timber.plant(DebugTree())

    StrictMode.setThreadPolicy(
      StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .penaltyDeath()
        .build(),
    )
    StrictMode.setVmPolicy(
      StrictMode.VmPolicy.Builder()
        .detectAll()
        .penaltyLog()
        // TODO: search bar
//        .penaltyDeath()
        .build(),
    )
  }
}
