package burrows.apps.example.gif.di.module

import burrows.apps.example.gif.BuildConfig
import burrows.apps.example.gif.data.RiffsyApiClient
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class RiffsyModule(var baseUrl: String = BuildConfig.BASE_URL) {
  @Provides fun providesRiffsyApi(retrofit: Retrofit.Builder)
    : RiffsyApiClient = retrofit
    .baseUrl(baseUrl)
    .build()
    .create(RiffsyApiClient::class.java)
}
