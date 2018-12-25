package com.burrowsapps.example.gif.di.module

import com.burrowsapps.example.gif.BuildConfig
import com.burrowsapps.example.gif.data.RiffsyApiClient
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class RiffsyModule(private var baseUrl: String = BuildConfig.BASE_URL) {
    @Provides fun providesRiffsyApi(retrofit: Retrofit.Builder): RiffsyApiClient = retrofit
        .baseUrl(baseUrl)
        .build()
        .create(RiffsyApiClient::class.java)
}
