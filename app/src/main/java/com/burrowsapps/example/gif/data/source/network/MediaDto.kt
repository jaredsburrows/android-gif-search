package com.burrowsapps.example.gif.data.source.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MediaDto(
  @field:Json(name = "tinygif")
  val tinyGifJson: GifDto? = GifDto(),
  @field:Json(name = "gif")
  val gifJson: GifDto? = GifDto()
) {
  val tinyGif: GifDto
    get() = tinyGifJson ?: GifDto()
  val gif: GifDto
    get() = gifJson ?: GifDto()
}
