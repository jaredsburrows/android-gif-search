package com.burrowsapps.gif.search.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * File formats of one size tier. The API also serves "webp", "mp4" and "webm" variants, which
 * the app does not consume: "gif" is the animated image and "jpg" is its static preview.
 */
@JsonClass(generateAdapter = true)
internal data class MediaDto(
  @field:Json(name = "gif")
  val gif: GifDto = GifDto(),
  @field:Json(name = "jpg")
  val jpg: GifDto = GifDto(),
)
