package com.burrowsapps.gif.search.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gifs")
internal data class GifEntity(
  @PrimaryKey val tinyGifUrl: String,
  val tinyGifPreviewUrl: String,
  val gifUrl: String,
  val gifPreviewUrl: String,
)
