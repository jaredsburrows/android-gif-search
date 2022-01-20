package com.burrowsapps.example.gif.di

import android.content.ClipboardManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/**
 * Injections for the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
  @Provides
  fun providesClipboardManager(@ApplicationContext context: Context): ClipboardManager {
    return context.applicationContext.getSystemService(ClipboardManager::class.java)
  }
}
