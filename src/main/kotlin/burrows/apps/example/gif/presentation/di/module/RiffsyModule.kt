package burrows.apps.example.gif.presentation.di.module

import burrows.apps.example.gif.data.rest.repository.RiffsyApiClient
import burrows.apps.example.gif.presentation.di.scope.PerActivity
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@Module
open class RiffsyModule {
  @Provides @PerActivity open fun providesRiffsyApi(builder: Retrofit.Builder): RiffsyApiClient {
    return builder
      .baseUrl(RiffsyApiClient.BASE_URL)
      .build()
      .create(RiffsyApiClient::class.java)
  }
}
