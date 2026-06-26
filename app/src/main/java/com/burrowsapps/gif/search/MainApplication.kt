package com.burrowsapps.gif.search

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The base application class for this app.
 *
 * Annotated with @HiltAndroidApp to generate the Hilt dependency-injection components for the whole
 * app. It is left `open` so the debug build can subclass it (see DebugApplication) to plant Timber
 * and install StrictMode.
 */
@HiltAndroidApp
open class MainApplication : Application()
