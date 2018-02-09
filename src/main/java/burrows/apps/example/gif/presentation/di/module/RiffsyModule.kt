package burrows.apps.example.gif.presentation.di.module

import burrows.apps.example.gif.BuildConfig
import burrows.apps.example.gif.data.repository.RiffsyApiClient
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@Module
class RiffsyModule {
  @Provides fun providesRiffsyApi(retrofit: Retrofit.Builder): RiffsyApiClient = retrofit
    .baseUrl(BuildConfig.BASE_URL)
    .build()
    .create(RiffsyApiClient::class.java)
}
