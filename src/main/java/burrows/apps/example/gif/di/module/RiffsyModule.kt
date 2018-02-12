package burrows.apps.example.gif.di.module

import burrows.apps.example.gif.BuildConfig
import burrows.apps.example.gif.data.RiffsyApiClient
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class RiffsyModule {
  @Provides fun providesRiffsyApi(retrofit: Retrofit.Builder)
    : RiffsyApiClient = retrofit
    .baseUrl(BuildConfig.BASE_URL)
    .build()
    .create(RiffsyApiClient::class.java)
}
