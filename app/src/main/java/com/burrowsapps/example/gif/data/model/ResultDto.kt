package com.burrowsapps.example.gif.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResultDto(
  @field:Json(name = "media")
  val mediaJson: List<MediaDto>? = emptyList(),
  @field:Json(name = "title")
  val titleJson: String? = ""
) {
  val media: List<MediaDto>
    get() = mediaJson.orEmpty()
  val title: String
    get() = titleJson.orEmpty()
}
