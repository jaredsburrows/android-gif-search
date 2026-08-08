package com.burrowsapps.gif.search.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Paged payload of a Klipy response: the list of results plus page-based pagination state.
 */
@JsonClass(generateAdapter = true)
internal data class DataDto(
  @field:Json(name = "data")
  val results: List<ResultDto> = emptyList(),
  @field:Json(name = "current_page")
  val currentPage: Int = 1,
  @field:Json(name = "per_page")
  val perPage: Int = 0,
  @field:Json(name = "has_next")
  val hasNext: Boolean = false,
)
