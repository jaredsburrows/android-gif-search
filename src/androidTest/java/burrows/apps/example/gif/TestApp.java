package burrows.apps.example.gif;

import burrows.apps.example.gif.di.component.AppComponent;
import burrows.apps.example.gif.di.component.NetComponent;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class TestApp extends App {
  public void setAppComponent(AppComponent appComponent) {
    this.appComponent = appComponent;
  }

  public void setRiffsyComponent(NetComponent netComponent) {
    this.netComponent = netComponent;
  }
}
