package com.burrowsapps.gif.search.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GifDto(
  @field:Json(name = "url")
  val url: String = "",
  @field:Json(name = "preview")
  val preview: String = "",
)
