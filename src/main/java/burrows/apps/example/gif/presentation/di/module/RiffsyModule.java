package burrows.apps.example.gif.presentation.di.module;

import burrows.apps.example.gif.data.rest.repository.RiffsyRepository;
import burrows.apps.example.gif.presentation.di.scope.PerActivity;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@Module
public class RiffsyModule {
  @Provides @PerActivity protected RiffsyRepository provideRiffsyApi(Retrofit.Builder retrofit) {
    return retrofit.baseUrl(RiffsyRepository.BASE_URL).build().create(RiffsyRepository.class);
  }
}
