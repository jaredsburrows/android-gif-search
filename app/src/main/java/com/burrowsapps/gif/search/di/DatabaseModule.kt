package com.burrowsapps.gif.search.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.burrowsapps.gif.search.data.db.AppDatabase
import com.burrowsapps.gif.search.data.db.dao.GifDao
import com.burrowsapps.gif.search.data.db.dao.QueryResultDao
import com.burrowsapps.gif.search.data.db.dao.RemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseModule {
  @Provides
  @Singleton
  fun provideDatabase(
    @ApplicationContext context: Context,
  ): AppDatabase =
    Room
      .databaseBuilder(context, AppDatabase::class.java, "gif-db")
      .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
      .fallbackToDestructiveMigration(false)
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
