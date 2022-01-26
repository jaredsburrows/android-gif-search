package com.burrowsapps.example.gif.data.source.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GifDto(
  @field:Json(name = "url")
  val url: String = "",
  @field:Json(name = "preview")
  val preview: String = ""
)
