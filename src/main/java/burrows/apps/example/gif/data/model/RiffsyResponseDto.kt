package burrows.apps.example.gif.data.model

import com.squareup.moshi.Json

/**
 * Riffsy Api Response.
 *
 * eg. https://api.riffsy.com/v1/search?key=LIVDSRZULELA&tag=goodluck&limit=10
 */
data class RiffsyResponseDto(
    @field:Json(name = "results") val _results: List<ResultDto>? = null,
    @field:Json(name = "next") val _next: Double? = null
) {
    val results: List<ResultDto>
        get() = _results.orEmpty()
    val next: Double
        get() = _next ?: 0.0
}
