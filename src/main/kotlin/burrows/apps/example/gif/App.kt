package burrows.apps.example.gif

import android.annotation.SuppressLint
import android.support.multidex.MultiDexApplication
import burrows.apps.example.gif.presentation.di.component.ActivityComponent
import burrows.apps.example.gif.presentation.di.component.AppComponent

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@SuppressLint("Registered")
open class App : MultiDexApplication() {
  lateinit var appComponent: AppComponent
  lateinit var activityComponent: ActivityComponent

  override fun onCreate() {
    super.onCreate()

    // Setup components
    appComponent = AppComponent.init(this)
    activityComponent = ActivityComponent.init(appComponent)
  }
}
