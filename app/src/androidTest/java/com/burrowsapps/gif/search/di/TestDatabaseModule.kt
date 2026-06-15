package com.burrowsapps.gif.search.di

import android.content.Context
import androidx.room.Room
import com.burrowsapps.gif.search.data.db.AppDatabase
import com.burrowsapps.gif.search.data.db.dao.GifDao
import com.burrowsapps.gif.search.data.db.dao.QueryResultDao
import com.burrowsapps.gif.search.data.db.dao.RemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * Replaces the on-disk database with an in-memory one for instrumented tests.
 *
 * The real database persists across reinstalls, so data left by manually running the app (or a
 * previous run) would survive into a test and shadow the MockWebServer fixtures via
 * RemoteMediator.initialize()'s SKIP_INITIAL_REFRESH. An in-memory database starts empty every test
 * process and is never written to disk, keeping tests deterministic.
 */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DatabaseModule::class])
internal class TestDatabaseModule {
  @Provides
  @Singleton
  fun provideDatabase(
    @ApplicationContext context: Context,
  ): AppDatabase =
    Room
      .inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .build()

  @Provides
  @Singleton
  fun provideGifDao(db: AppDatabase): GifDao = db.gifDao()

  @Provides
  @Singleton
  fun provideQueryResultDao(db: AppDatabase): QueryResultDao = db.queryResultDao()

  @Provides
  @Singleton
  fun provideRemoteKeysDao(db: AppDatabase): RemoteKeysDao = db.remoteKeysDao()
}
