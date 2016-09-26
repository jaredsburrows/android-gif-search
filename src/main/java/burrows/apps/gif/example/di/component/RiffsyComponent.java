package burrows.apps.gif.example.di.component;

import burrows.apps.gif.example.di.module.RiffsyModule;
import burrows.apps.gif.example.di.scope.PerActivity;
import burrows.apps.gif.example.rest.service.RiffsyService;
import burrows.apps.gif.example.ui.adapter.GifAdapter;
import burrows.apps.gif.example.ui.fragment.MainFragment;
import dagger.Component;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@PerActivity
@Component(dependencies = AppComponent.class, modules = RiffsyModule.class)
public interface RiffsyComponent {
  // Injections
  void inject(final MainFragment mainFragment);
  void inject(final GifAdapter gifAdapter);

  // Expose to subgraphs
  RiffsyService riffsyService();

  // Setup components dependencies and modules
  final class Builder {
    private Builder() {
    }

    public static RiffsyComponent build(final AppComponent appComponent) {
      return DaggerRiffsyComponent.builder()
        .appComponent(appComponent)
        .build();
    }
  }
}
