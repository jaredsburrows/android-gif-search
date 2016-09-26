package burrows.apps.gif.example;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.VmPolicy;
import android.support.v7.app.AppCompatDelegate;
import burrows.apps.gif.example.di.component.AppComponent;
import burrows.apps.gif.example.di.component.RiffsyComponent;
import burrows.apps.gif.example.rx.RxBus;
import com.squareup.leakcanary.RefWatcher;

import javax.inject.Inject;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class App extends Application {
  private App app;
  private AppComponent appComponent;
  private RiffsyComponent riffsyComponent;
  @Inject RxBus bus;
  @Inject RefWatcher refWatcher;

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

    app = this;

    // Setup components
    initAppComponent();
    appComponent.inject(this);
    initRiffsyComponent();

    // Make sure we use vector drawables
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
  }

  /**
   * Use this method to watch the reference to context in onDestroy methods.
   *
   * @param context Context.
   * @return Instance of RefWatcher.
   */
  public static RefWatcher getRefWatcher(final Context context) {
    return ((App) context.getApplicationContext()).refWatcher;
  }

  // App Component
  public void initAppComponent() {
    appComponent = AppComponent.Builder.build(app);
  }

  public AppComponent getAppComponent() {
    return appComponent;
  }

  // Riffsy Component
  public void initRiffsyComponent() {
    riffsyComponent = RiffsyComponent.Builder.build(appComponent);
  }

  public RiffsyComponent getRiffsyComponent() {
    return riffsyComponent;
  }
}
