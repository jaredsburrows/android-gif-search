package burrows.apps.example.gif;

import android.app.Application;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.VmPolicy;
import android.support.v7.app.AppCompatDelegate;
import burrows.apps.example.gif.di.component.AppComponent;
import burrows.apps.example.gif.di.component.NetComponent;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class App extends Application {
  protected AppComponent appComponent;
  protected NetComponent netComponent;

  @Override public void onCreate() {
    // Let's start paying critical attention to issues via Logcat
    if (BuildConfig.DEBUG) {
      StrictMode.setThreadPolicy(new ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build());
      StrictMode.setVmPolicy(new VmPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build());
    }
    super.onCreate();

    // Setup components
    initAppComponent();
    initNetComponent();

    // Make sure we use vector drawables
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
  }

  // App Component
  public void initAppComponent() {
    appComponent = AppComponent.Builder.build(this);
  }

  public AppComponent getAppComponent() {
    return appComponent;
  }

  // Riffsy Component
  public void initNetComponent() {
    netComponent = NetComponent.Builder.build(appComponent);
  }

  public NetComponent getNetComponent() {
    return netComponent;
  }
}
