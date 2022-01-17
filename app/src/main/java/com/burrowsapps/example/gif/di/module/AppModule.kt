package com.burrowsapps.example.gif.di.module

import android.app.Application
import android.content.Context
import com.burrowsapps.example.gif.AppCoroutineDispatchers
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
  @Binds
  abstract fun provideContext(application: Application): Context

  @Singleton
  @Provides
  fun provideCoroutineDispatchers() = AppCoroutineDispatchers(
    io = Dispatchers.IO,
    computation = Dispatchers.Default,
    main = Dispatchers.Main
  )
}
