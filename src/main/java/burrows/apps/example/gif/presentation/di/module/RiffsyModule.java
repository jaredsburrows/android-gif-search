package burrows.apps.example.gif.presentation.di.module;

import burrows.apps.example.gif.data.rest.repository.RiffsyApiClient;
import burrows.apps.example.gif.presentation.di.scope.PerActivity;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@Module
public class RiffsyModule {
  @Provides @PerActivity protected RiffsyApiClient provideRiffsyApi(Retrofit.Builder builder) {
    return builder.baseUrl(RiffsyApiClient.BASE_URL).build().create(RiffsyApiClient.class);
  }
}
