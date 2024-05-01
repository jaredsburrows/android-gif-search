package com.burrowsapps.gif.search.di

import com.burrowsapps.gif.search.di.ApplicationMode.TESTING
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

/** Injections for the API. */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [AppConfigModule::class])
internal class TestAppConfigModule {
  @Provides
  fun provideApplicationMode(): ApplicationMode {
    return TESTING
  }
}
