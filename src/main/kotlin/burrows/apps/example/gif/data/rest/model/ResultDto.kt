package burrows.apps.example.gif.data.rest.model

import com.squareup.moshi.Json

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
data class ResultDto(
  @Json(name = "media") var media: List<MediaDto>? = null,
  @Json(name = "title") var title: String? = null)
