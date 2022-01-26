package com.burrowsapps.example.gif.data.source.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Tenor Api Response.
 *
 * eg. https://g.tenor.com/v1/search?key=LIVDSRZULELA&tag=goodluck&limit=10
 */
@JsonClass(generateAdapter = true)
data class TenorResponseDto(
  @field:Json(name = "results")
  val results: List<ResultDto> = emptyList(),
  @field:Json(name = "next")
  val next: String = "0.0"
)
