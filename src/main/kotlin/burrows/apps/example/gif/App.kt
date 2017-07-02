package burrows.apps.example.gif

import android.annotation.SuppressLint
import android.app.Application
import burrows.apps.example.gif.presentation.di.component.ActivityComponent
import burrows.apps.example.gif.presentation.di.component.AppComponent

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@SuppressLint("Registered")
open class App : Application() {
  open lateinit var appComponent: AppComponent
  open lateinit var activityComponent: ActivityComponent

  override fun onCreate() {
    super.onCreate()

    // Setup components
    appComponent = AppComponent.init(this)
    activityComponent = ActivityComponent.init(appComponent)
  }
}
