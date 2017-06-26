package burrows.apps.example.gif

import burrows.apps.example.gif.presentation.di.component.ActivityComponent
import burrows.apps.example.gif.presentation.di.component.AppComponent

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class TestApp : App() {
  override var appComponent: AppComponent?
    get() = super.appComponent
    set(appComponent) {
      this.appComponent = appComponent
    }

  fun setRiffsyComponent(netComponent: ActivityComponent) {
    this.activityComponent = netComponent
  }
}
