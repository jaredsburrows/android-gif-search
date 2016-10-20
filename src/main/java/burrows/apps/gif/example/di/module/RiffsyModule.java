package burrows.apps.gif.example.di.module;


import burrows.apps.gif.example.di.scope.PerActivity;
import burrows.apps.gif.example.rest.service.RiffsyRepository;
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
