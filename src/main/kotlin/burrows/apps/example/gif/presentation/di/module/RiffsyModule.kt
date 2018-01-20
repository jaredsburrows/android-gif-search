package burrows.apps.example.gif.presentation.di.module

import burrows.apps.example.gif.data.repository.RiffsyApiClient
import burrows.apps.example.gif.presentation.di.scope.PerActivity
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@Module
class RiffsyModule(val url: String) {
  companion object {
    private const val RIFFSY_URL = "https://api.riffsy.com"
  }

  constructor() : this(RIFFSY_URL)

  @Provides @PerActivity fun providesRiffsyApi(retrofit: Retrofit.Builder): RiffsyApiClient {
    return retrofit
      .baseUrl(url)
      .build()
      .create(RiffsyApiClient::class.java)
  }
}
