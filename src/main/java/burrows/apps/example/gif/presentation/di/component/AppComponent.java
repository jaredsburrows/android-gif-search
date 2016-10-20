package burrows.apps.example.gif.presentation.di.component;

import android.app.Application;
import android.content.Context;
import burrows.apps.example.gif.App;
import burrows.apps.example.gif.presentation.di.module.AppModule;
import dagger.Component;

import javax.inject.Singleton;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
  // Injections
  void inject(App app);

  // Expose to subgraphs
  Application application();
  Context context();

  // Setup components dependencies and modules
  final class Initializer {
    private Initializer() {
    }

    public static AppComponent init(Application application) {
      return DaggerAppComponent.builder()
        .appModule(new AppModule(application))
        .build();
    }
  }
}
