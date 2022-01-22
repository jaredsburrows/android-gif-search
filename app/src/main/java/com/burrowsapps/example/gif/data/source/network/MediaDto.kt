package com.burrowsapps.example.gif.data.source.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MediaDto(
  @field:Json(name = "tinygif")
  val gifJson: GifDto? = GifDto()
) {
  val gif: GifDto
    get() = gifJson ?: GifDto()
}
