package com.burrowsapps.example.gif

import android.annotation.SuppressLint
import com.burrowsapps.example.gif.di.component.DaggerAppComponent
import dagger.android.DaggerApplication

@SuppressLint("Registered")
open class App : DaggerApplication() {
  override fun applicationInjector() = DaggerAppComponent.builder()
    .application(this)
    .build()
}
