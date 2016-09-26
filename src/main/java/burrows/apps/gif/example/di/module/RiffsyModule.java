package burrows.apps.gif.example.di.module;


import burrows.apps.gif.example.di.scope.PerActivity;
import burrows.apps.gif.example.rest.service.RiffsyService;
import dagger.Module;
import dagger.Provides;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@Module
public class RiffsyModule {
  @Provides @PerActivity RiffsyService provideRiffsyService() {
    return new RiffsyService();
  }
}
