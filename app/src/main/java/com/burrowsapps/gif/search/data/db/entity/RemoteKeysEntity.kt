package com.burrowsapps.gif.search.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
internal data class RemoteKeysEntity(
  @PrimaryKey val searchKey: String,
  val nextKey: String?,
)
