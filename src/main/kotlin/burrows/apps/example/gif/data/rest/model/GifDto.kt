package burrows.apps.example.gif.data.rest.model

import com.squareup.moshi.Json

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
data class GifDto(
  @Json(name = "url") var url: String? = null,
  @Json(name = "preview") var preview: String? = null)
