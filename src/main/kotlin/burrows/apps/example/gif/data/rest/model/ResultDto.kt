package burrows.apps.example.gif.data.rest.model

import com.squareup.moshi.Json

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class ResultDto {
  @Json(name = "media") var media: List<MediaDto>? = null
  @Json(name = "title") var title: String? = null

  /**
   * No args constructor for use in serialization.
   */
  @Suppress("ConvertSecondaryConstructorToPrimary")
  constructor()
}
