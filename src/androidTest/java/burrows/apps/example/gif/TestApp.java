package burrows.apps.example.gif;

import burrows.apps.example.gif.di.component.AppComponent;
import burrows.apps.example.gif.di.component.ActivityComponent;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class TestApp extends App {
  public void setAppComponent(AppComponent appComponent) {
    this.appComponent = appComponent;
  }

  public void setRiffsyComponent(ActivityComponent netComponent) {
    this.activityComponent = netComponent;
  }
}
