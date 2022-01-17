package com.burrowsapps.example.gif

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy

class DebugApplication : Application() {
  override fun onCreate() {
    StrictMode.setThreadPolicy(
      ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build()
    )
    StrictMode.setVmPolicy(
      VmPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build()
    )

    super.onCreate()
  }
}
