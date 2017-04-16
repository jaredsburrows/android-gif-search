package burrows.apps.example.gif;

import android.annotation.SuppressLint;
import android.app.Application;
import burrows.apps.example.gif.presentation.di.component.ActivityComponent;
import burrows.apps.example.gif.presentation.di.component.AppComponent;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@SuppressLint("Registered")
public class App extends Application {
  public AppComponent appComponent;
  public ActivityComponent activityComponent;

  @Override public void onCreate() {
    super.onCreate();

    // Setup components
    appComponent = AppComponent.Initializer.init(this);
    activityComponent = ActivityComponent.Initializer.init(appComponent);
  }
}
