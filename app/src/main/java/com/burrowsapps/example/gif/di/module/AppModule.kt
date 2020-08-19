package com.burrowsapps.example.gif.di.module

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class AppModule {
  @Binds abstract fun provideContext(application: Application): Context
}
