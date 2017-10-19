package burrows.apps.example.gif.data.rest.model

/**
 * Riffsy Api Response.
 * eg. https://api.riffsy.com/v1/search?key=LIVDSRZULELA&tag=goodluck&limit=10
 *
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
data class RiffsyResponseDto(
  var results: List<ResultDto>? = emptyList(),
  var page: Double? = null)
