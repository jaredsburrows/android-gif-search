package burrows.apps.example.gif;

import android.app.Application;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.VmPolicy;
import android.support.v7.app.AppCompatDelegate;
import burrows.apps.example.gif.presentation.di.component.ActivityComponent;
import burrows.apps.example.gif.presentation.di.component.AppComponent;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
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
