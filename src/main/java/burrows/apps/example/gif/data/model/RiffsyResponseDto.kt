package burrows.apps.example.gif.data.model

/**
 * Riffsy Api Response.
 *
 * eg. https://api.riffsy.com/v1/search?key=LIVDSRZULELA&tag=goodluck&limit=10
 */
data class RiffsyResponseDto(
  var results: List<ResultDto>? = emptyList(),
  var next: Double? = null)
