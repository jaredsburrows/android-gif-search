package burrows.apps.gif.example;

import burrows.apps.gif.example.di.component.AppComponent;
import burrows.apps.gif.example.di.component.NetComponent;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class TestApp extends App {
  public void setAppComponent(final AppComponent appComponent) {
    this.appComponent = appComponent;
  }

  public void setRiffsyComponent(final NetComponent riffsyComponent) {
    this.netComponent = riffsyComponent;
  }
}
