package com.burrowsapps.gif.search.data.db.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import com.burrowsapps.gif.search.data.db.entity.GifEntity

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
  val gifId: String, // references GifEntity.tinyGifUrl
  val position: Long,
)
