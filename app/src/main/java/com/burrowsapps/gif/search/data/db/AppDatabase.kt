package com.burrowsapps.gif.search.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.burrowsapps.gif.search.data.db.dao.GifDao
import com.burrowsapps.gif.search.data.db.dao.QueryResultDao
import com.burrowsapps.gif.search.data.db.dao.RemoteKeysDao
import com.burrowsapps.gif.search.data.db.entity.GifEntity
import com.burrowsapps.gif.search.data.db.entity.QueryResultEntity
import com.burrowsapps.gif.search.data.db.entity.RemoteKeysEntity

@Database(
  entities = [GifEntity::class, QueryResultEntity::class, RemoteKeysEntity::class],
  version = 1,
  exportSchema = false,
)
internal abstract class AppDatabase : RoomDatabase() {
  abstract fun gifDao(): GifDao

  abstract fun queryResultDao(): QueryResultDao

  abstract fun remoteKeysDao(): RemoteKeysDao
}
