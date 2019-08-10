package com.burrowsapps.example.gif.di.module

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module

@Module
abstract class AppModule {
  @Binds abstract fun providesContext(application: Application): Context
}
