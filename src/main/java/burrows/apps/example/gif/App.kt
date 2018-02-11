package burrows.apps.example.gif

import android.annotation.SuppressLint
import burrows.apps.example.gif.di.component.DaggerAppComponent
import dagger.android.DaggerApplication

@SuppressLint("Registered")
open class App : DaggerApplication() {
  override fun applicationInjector() = DaggerAppComponent.builder()
    .application(this)
    .build()
}
