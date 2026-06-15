package com.burrowsapps.gif.search

import android.os.StrictMode
import timber.log.Timber
import timber.log.Timber.DebugTree

class DebugApplication : MainApplication() {
  override fun onCreate() {
    super.onCreate()

    Timber.plant(DebugTree())

    StrictMode.setThreadPolicy(
      StrictMode.ThreadPolicy
        .Builder()
        .detectAll()
        .penaltyLog()
        .penaltyDeath()
        .build(),
    )
    // This is detectAll() minus detectIncorrectContextUse(). Once the license screen's WebView is
    // created, WebView/Chromium registers an application-scoped ComponentCallbacks that reads
    // ViewConfiguration from a non-UI context on every configuration change, raising an
    // IncorrectContextUseViolation (https://issuetracker.google.com/issues/296928070). That is not
    // fixable from app code and would kill penaltyDeath() on rotation, so it is the only detector
    // left off; everything else is fatal.
    StrictMode.setVmPolicy(
      StrictMode.VmPolicy
        .Builder()
        .detectActivityLeaks()
        .detectLeakedClosableObjects()
        .detectLeakedRegistrationObjects()
        .detectLeakedSqlLiteObjects()
        .detectContentUriWithoutPermission()
        .detectFileUriExposure()
        .detectCleartextNetwork()
        .detectUntaggedSockets()
        .detectCredentialProtectedWhileLocked()
        .detectImplicitDirectBoot()
        .detectUnsafeIntentLaunch()
        .penaltyLog()
        .penaltyDeath()
        .build(),
    )
  }
}
