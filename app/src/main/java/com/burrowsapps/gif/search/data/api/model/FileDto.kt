package com.burrowsapps.gif.search.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Size tiers of a Klipy result's files. The API also serves "hd" and "xs" tiers, which the
 * app does not consume: "md" backs the full-size dialog and "sm" backs the grid.
 */
@JsonClass(generateAdapter = true)
internal data class FileDto(
  @field:Json(name = "md")
  val md: MediaDto = MediaDto(),
  @field:Json(name = "sm")
  val sm: MediaDto = MediaDto(),
)
