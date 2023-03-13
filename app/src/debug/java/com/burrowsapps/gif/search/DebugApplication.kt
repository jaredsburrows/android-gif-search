package com.burrowsapps.gif.search

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import timber.log.Timber
import timber.log.Timber.DebugTree

class DebugApplication : MainApplication() {
  override fun onCreate() {
    super.onCreate()

    Timber.plant(DebugTree())

    StrictMode.setThreadPolicy(
      ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build(),
    )
    StrictMode.setVmPolicy(
      VmPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build(),
    )
  }
}
