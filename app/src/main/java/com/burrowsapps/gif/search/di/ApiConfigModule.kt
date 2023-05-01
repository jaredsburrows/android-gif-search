package com.burrowsapps.gif.search.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Injections for the API. */
@Module
@InstallIn(SingletonComponent::class)
internal class ApiConfigModule {
  @Provides
  @Singleton
  fun provideBaseUrl(): String {
    return "https://g.tenor.com"
  }
}
