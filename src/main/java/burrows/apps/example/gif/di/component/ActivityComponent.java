package burrows.apps.example.gif.di.component;

import burrows.apps.example.gif.data.rest.repository.ImageRepository;
import burrows.apps.example.gif.data.rest.repository.RiffsyRepository;
import burrows.apps.example.gif.di.module.GlideModule;
import burrows.apps.example.gif.di.module.NetModule;
import burrows.apps.example.gif.di.module.RiffsyModule;
import burrows.apps.example.gif.di.module.SchedulerProviderModule;
import burrows.apps.example.gif.di.scope.PerActivity;
import burrows.apps.example.gif.presentation.main.MainActivity;
import burrows.apps.example.gif.presentation.main.MainFragment;
import burrows.apps.example.gif.ui.adapter.GifAdapter;
import dagger.Component;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@PerActivity
@Component(dependencies = AppComponent.class, modules = {NetModule.class, RiffsyModule.class, GlideModule.class, SchedulerProviderModule.class})
public interface ActivityComponent {
  // Injections
  void inject(MainActivity mainActivity);
  void inject(MainFragment mainFragment);
  void inject(GifAdapter gifAdapter);

  // Expose to subgraphs
  RiffsyRepository riffsyService();
  ImageRepository imageRepository();

  // Setup components dependencies and modules
  final class Builder {
    private Builder() {
    }

    public static ActivityComponent build(AppComponent appComponent) {
      return DaggerActivityComponent.builder()
        .appComponent(appComponent)
        .build();
    }
  }
}
