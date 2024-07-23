package com.burrowsapps.gif.search

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The main application class for this app.
 *
 * This class is annotated with @HiltAndroidApp, which enables the Hilt dependency injection
 * framework for the entire app. It also extends the Application class, and overrides its onCreate()
 * method to set the default night mode and enable compatibility mode for vector drawables.
 */
@HiltAndroidApp
open class MainApplication : Application()
