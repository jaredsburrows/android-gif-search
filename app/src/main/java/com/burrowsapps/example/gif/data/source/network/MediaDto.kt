package com.burrowsapps.example.gif.data.source.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class MediaDto(
  @field:Json(name = "gif")
  val gif: GifDto = GifDto(),
  @field:Json(name = "tinygif")
  val tinyGif: GifDto = GifDto(),
)
