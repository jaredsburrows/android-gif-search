package burrows.apps.example.gif.presentation.di.module

import burrows.apps.example.gif.BuildConfig
import burrows.apps.example.gif.data.rest.repository.RiffsyApiClient
import burrows.apps.example.gif.presentation.di.scope.PerActivity
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@Module
class RiffsyModule {
  @Provides @PerActivity fun providesRiffsyApi(retrofit: Retrofit.Builder): RiffsyApiClient {
    return retrofit
      .baseUrl(BuildConfig.BASE_URL)
      .build()
      .create(RiffsyApiClient::class.java)
  }
}
