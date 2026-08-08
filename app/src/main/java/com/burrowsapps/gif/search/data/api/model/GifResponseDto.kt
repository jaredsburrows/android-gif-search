package com.burrowsapps.gif.search.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Klipy Api Response.
 *
 * eg. https://api.klipy.com/api/v1/{app_key}/gifs/search?q=hello&page=1&per_page=10
 */
@JsonClass(generateAdapter = true)
internal data class GifResponseDto(
  @field:Json(name = "result")
  val result: Boolean = false,
  @field:Json(name = "data")
  val data: DataDto = DataDto(),
)
