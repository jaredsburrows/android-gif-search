package com.burrowsapps.example.gif.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GifDto(
  @field:Json(name = "url") val urlJson: String? = "",
  @field:Json(name = "preview") val urlPreview: String? = ""
) {
  val url: String
    get() = urlJson.orEmpty()
  val preview: String
    get() = urlPreview.orEmpty()
}
