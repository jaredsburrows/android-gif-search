package burrows.apps.example.gif.di.module;


import burrows.apps.example.gif.di.scope.PerActivity;
import burrows.apps.example.gif.rest.service.RiffsyRepository;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@Module
public class RiffsyModule {
  @Provides @PerActivity protected RiffsyRepository provideRiffsyService(Retrofit.Builder retrofit) {
    return new RiffsyRepository(retrofit);
  }
}
