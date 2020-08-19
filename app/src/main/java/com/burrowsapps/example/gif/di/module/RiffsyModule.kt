package com.burrowsapps.example.gif.di.module

import com.burrowsapps.example.gif.BuildConfig
import com.burrowsapps.example.gif.data.RiffsyApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import retrofit2.Retrofit

@Module
@InstallIn(ActivityComponent::class)
class RiffsyModule(private var baseUrl: String = BuildConfig.BASE_URL) {
  @Provides fun provideRiffsyApi(retrofit: Retrofit.Builder): RiffsyApiClient = retrofit
    .baseUrl(baseUrl)
    .build()
    .create(RiffsyApiClient::class.java)
}
