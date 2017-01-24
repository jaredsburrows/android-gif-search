package burrows.apps.example.gif.presentation.di.component;

import burrows.apps.example.gif.presentation.di.module.GlideModule;
import burrows.apps.example.gif.presentation.di.module.LeakCanaryModule;
import burrows.apps.example.gif.presentation.di.module.NetModule;
import burrows.apps.example.gif.presentation.di.module.RiffsyModule;
import burrows.apps.example.gif.presentation.di.module.SchedulerProviderModule;
import burrows.apps.example.gif.presentation.di.scope.PerActivity;
import burrows.apps.example.gif.presentation.main.MainActivity;
import burrows.apps.example.gif.presentation.main.MainFragment;
import burrows.apps.example.gif.presentation.adapter.GifAdapter;
import dagger.Component;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@PerActivity @Component(dependencies = AppComponent.class, modules = {NetModule.class, RiffsyModule.class,
  GlideModule.class, SchedulerProviderModule.class, LeakCanaryModule.class})
public interface ActivityComponent {
  // Injections
  void inject(MainActivity mainActivity);

  void inject(MainFragment mainFragment);

  void inject(GifAdapter gifAdapter);

  // Setup components dependencies and modules
  final class Initializer {
    private Initializer() {
    }

    public static ActivityComponent init(AppComponent appComponent) {
      return DaggerActivityComponent.builder()
        .appComponent(appComponent)
        .build();
    }
  }
}
