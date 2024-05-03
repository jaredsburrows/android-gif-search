package com.burrowsapps.gif.search.test

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication
import timber.log.Timber

/**
 * Custom JUnit runner for Hilt tests.
 *
 * This class extends the standard AndroidJUnitRunner and overrides the newApplication() method
 * to return a new instance of HiltTestApplication, which is a subclass of Application that is
 * annotated with @HiltAndroidApp.
 */
@Suppress("unused") // This is used in app/build.gradle.kts
class CustomTestRunner : AndroidJUnitRunner() {
  override fun newApplication(
    cl: ClassLoader?,
    name: String?,
    context: Context?,
  ): Application {
    Timber.plant(Timber.DebugTree()) // Enable Timber logging

    return super.newApplication(cl, HiltTestApplication::class.java.name, context)
  }
}
