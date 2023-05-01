package com.burrowsapps.gif.search.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/** Test overrides injections for the API. */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [ApiConfigModule::class])
internal class TestApiConfigModule {
  @Provides
  @Singleton
  fun provideBaseUrl(): String {
    return "http://localhost:8080"
  }
}
