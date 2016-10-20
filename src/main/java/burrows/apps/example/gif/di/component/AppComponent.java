package burrows.apps.example.gif.di.component;

import android.app.Application;
import android.content.Context;
import burrows.apps.example.gif.App;
import burrows.apps.example.gif.di.module.AppModule;
import burrows.apps.example.gif.di.module.LeakCanaryModule;
import com.squareup.leakcanary.RefWatcher;
import dagger.Component;

import javax.inject.Singleton;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@Singleton
@Component(modules = {AppModule.class, LeakCanaryModule.class})
public interface AppComponent {
  // Injections
  void inject(App app);

  // Expose to subgraphs
  Application application();
  Context context();
  RefWatcher refWatcher();

  // Setup components dependencies and modules
  final class Builder {
    private Builder() {
    }

    public static AppComponent build(Application application) {
      return DaggerAppComponent.builder()
        .appModule(new AppModule(application))
        .leakCanaryModule(new LeakCanaryModule(application))
        .build();
    }
  }
}
