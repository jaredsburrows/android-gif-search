package com.burrowsapps.gif.search.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.burrowsapps.gif.search.data.db.entity.RemoteKeysEntity

@Dao
internal interface RemoteKeysDao {
  @Query("SELECT * FROM remote_keys WHERE searchKey = :searchKey LIMIT 1")
  suspend fun remoteKeys(searchKey: String): RemoteKeysEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(keys: RemoteKeysEntity)

  @Query("DELETE FROM remote_keys WHERE searchKey = :searchKey")
  suspend fun clearQuery(searchKey: String)

  // Companion to QueryResultDao.clearStaleQueries: drops the cursor rows for the same stale searches
  // so remote_keys doesn't grow unbounded either. Skips [exceptKey] (the query currently loading).
  @Query("DELETE FROM remote_keys WHERE searchKey != :exceptKey AND lastUpdated < :cutoff")
  suspend fun clearStale(
    cutoff: Long,
    exceptKey: String,
  ): Int
}
