package burrows.apps.example.gif

import android.annotation.SuppressLint
import android.content.Context
import android.support.multidex.MultiDex
import burrows.apps.example.gif.presentation.di.component.DaggerAppComponent
import dagger.android.DaggerApplication

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@SuppressLint("Registered")
open class App : DaggerApplication() {
  override fun attachBaseContext(base: Context) {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }

  override fun applicationInjector() = DaggerAppComponent.builder().application(this).build()
}
