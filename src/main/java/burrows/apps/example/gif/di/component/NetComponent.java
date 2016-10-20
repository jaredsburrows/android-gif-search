package burrows.apps.example.gif.di.component;

import burrows.apps.example.gif.di.module.GlideModule;
import burrows.apps.example.gif.di.module.NetModule;
import burrows.apps.example.gif.di.module.RiffsyModule;
import burrows.apps.example.gif.di.scope.PerActivity;
import burrows.apps.example.gif.rest.service.ImageDownloader;
import burrows.apps.example.gif.rest.service.RiffsyRepository;
import burrows.apps.example.gif.ui.adapter.GifAdapter;
import burrows.apps.example.gif.ui.fragment.MainFragment;
import dagger.Component;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@PerActivity
@Component(dependencies = AppComponent.class, modules = {NetModule.class, RiffsyModule.class, GlideModule.class})
public interface NetComponent {
  // Injections
  void inject(MainFragment mainFragment);
  void inject(GifAdapter gifAdapter);

  // Expose to subgraphs
  RiffsyRepository riffsyService();
  ImageDownloader imageDownloader();

  // Setup components dependencies and modules
  final class Builder {
    private Builder() {
    }

    public static NetComponent build(AppComponent appComponent) {
      return DaggerNetComponent.builder()
        .appComponent(appComponent)
        .build();
    }
  }
}
