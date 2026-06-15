package com.burrowsapps.gif.search

import android.os.StrictMode
import android.os.strictmode.IncorrectContextUseViolation
import android.os.strictmode.Violation
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
    // VmPolicy can't use penaltyDeath() directly (and there is no detectAll()-minus-one API): once
    // the license screen's WebView is created, WebView/Chromium raises an IncorrectContextUseViolation
    // from an application-scoped callback on every configuration change. Keep detectAll() + log, and
    // crash on every violation via penaltyListener EXCEPT that one framework issue (see isFatalViolation).
    StrictMode.setVmPolicy(
      StrictMode.VmPolicy
        .Builder()
        .detectAll()
        .penaltyLog()
        .penaltyListener(mainExecutor) { violation ->
          if (isFatalViolation(violation)) throw violation
        }.build(),
    )
  }

  internal companion object {
    /**
     * Whether a [Violation] should crash the debug build (the penaltyDeath() equivalent).
     *
     * Every VmPolicy violation is fatal except [IncorrectContextUseViolation]. Once the license
     * screen's WebView exists, WebView/Chromium registers an application-scoped ComponentCallbacks
     * that reads ViewConfiguration from a non-UI context on every configuration change; that fires
     * on rotation, is not fixable from app code (https://issuetracker.google.com/issues/296928070),
     * and so is logged rather than fatal.
     */
    internal fun isFatalViolation(violation: Violation): Boolean = violation !is IncorrectContextUseViolation
  }
}
