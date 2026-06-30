package com.burrowsapps.gif.search.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
internal data class RemoteKeysEntity(
  @PrimaryKey val searchKey: String,
  val nextKey: String?,
  // Wall-clock time of the last successful fetch for this query; used by the staleness/TTL check.
  val lastUpdated: Long = 0L,
)
