package com.burrowsapps.example.gif.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MediaDto(
  @field:Json(name = "tinygif") val gifJson: GifDto? = GifDto() // Bug in 1.5.0
) {
  val gif: GifDto
    get() = gifJson ?: GifDto()
}
