package burrows.apps.example.gif

import burrows.apps.example.gif.presentation.di.component.ActivityComponent

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class TestApp : App() {
  fun setRiffsyComponent(netComponent: ActivityComponent) {
    this.activityComponent = netComponent
  }
}
