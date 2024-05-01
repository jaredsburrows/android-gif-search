package com.burrowsapps.gif.search.di

import com.burrowsapps.gif.search.di.ApplicationMode.NORMAL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

enum class ApplicationMode {
  NORMAL,
  TESTING,
}

/** Injections for the API. */
@Module
@InstallIn(SingletonComponent::class)
internal class AppConfigModule {
  @Provides
  fun provideApplicationMode(): ApplicationMode {
    return NORMAL
  }
}
