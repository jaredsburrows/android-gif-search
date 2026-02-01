package com.burrowsapps.gif.search.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
  tableName = "query_results",
  primaryKeys = ["searchKey", "gifId"],
  indices = [
    Index(value = ["searchKey", "position"], unique = true),
    Index(value = ["gifId"]),
  ],
  foreignKeys = [
    ForeignKey(
      entity = GifEntity::class,
      parentColumns = ["tinyGifUrl"],
      childColumns = ["gifId"],
      onDelete = ForeignKey.CASCADE,
      onUpdate = ForeignKey.CASCADE,
    ),
  ],
)
internal data class QueryResultEntity(
  val searchKey: String,
  // references GifEntity.tinyGifUrl
  val gifId: String,
  val position: Long,
)
