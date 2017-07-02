package burrows.apps.example.gif.data.rest.model

import com.squareup.moshi.Json

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class Result {
  @Json(name = "media") var media: List<Media>? = null
  @Json(name = "title") var title: String? = null

  /**
   * No args constructor for use in serialization.
   */
  @Suppress("ConvertSecondaryConstructorToPrimary")
  constructor()
}
