package com.burrowsapps.gif.search.di

import com.burrowsapps.gif.search.di.ApplicationMode.TESTING
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/** Injections for the API. */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [AppConfigModule::class])
internal class TestAppConfigModule {
  @Provides
  @Singleton
  fun provideApplicationMode(): ApplicationMode {
    return TESTING
  }
}
