package com.burrowsapps.gif.search.data.source.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ResultDto(
  @field:Json(name = "media")
  val media: List<MediaDto> = emptyList(),
)
