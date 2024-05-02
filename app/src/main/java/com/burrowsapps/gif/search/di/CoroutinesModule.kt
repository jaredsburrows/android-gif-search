package com.burrowsapps.gif.search.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.BINARY

@Retention(BINARY)
@Qualifier
internal annotation class DefaultDispatcher

@Retention(BINARY)
@Qualifier
internal annotation class IoDispatcher

@Retention(BINARY)
@Qualifier
internal annotation class MainDispatcher

@Retention(BINARY)
@Qualifier
internal annotation class MainImmediateDispatcher

@Module
@InstallIn(SingletonComponent::class)
internal class CoroutinesModule {
  @DefaultDispatcher
  @Provides
  fun provideDefaultDispatcher(): CoroutineDispatcher = Default

  @IoDispatcher
  @Provides
  fun provideIoDispatcher(): CoroutineDispatcher = IO

  @MainDispatcher
  @Provides
  fun provideMainDispatcher(): CoroutineDispatcher = Main

  @MainImmediateDispatcher
  @Provides
  fun provideMainImmediateDispatcher(): CoroutineDispatcher = Main.immediate
}
