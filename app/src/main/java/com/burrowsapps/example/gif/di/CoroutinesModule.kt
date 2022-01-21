package com.burrowsapps.example.gif.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

@Module
@InstallIn(SingletonComponent::class)
object CoroutinesModule {
  @DefaultDispatcher
  @Provides
  fun providesDefaultDispatcher(): CoroutineDispatcher = Default

  @IoDispatcher
  @Provides
  fun providesIoDispatcher(): CoroutineDispatcher = IO

  @MainDispatcher
  @Provides
  fun providesMainDispatcher(): CoroutineDispatcher = Main

  @MainImmediateDispatcher
  @Provides
  fun providesMainImmediateDispatcher(): CoroutineDispatcher = Main.immediate
}
