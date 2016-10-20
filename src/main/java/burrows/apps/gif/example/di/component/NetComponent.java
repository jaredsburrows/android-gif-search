package burrows.apps.gif.example.di.component;

import burrows.apps.gif.example.di.module.GlideModule;
import burrows.apps.gif.example.di.module.NetModule;
import burrows.apps.gif.example.di.module.RiffsyModule;
import burrows.apps.gif.example.di.scope.PerActivity;
import burrows.apps.gif.example.rest.service.ImageDownloader;
import burrows.apps.gif.example.rest.service.RiffsyRepository;
import burrows.apps.gif.example.ui.adapter.GifAdapter;
import burrows.apps.gif.example.ui.fragment.MainFragment;
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
