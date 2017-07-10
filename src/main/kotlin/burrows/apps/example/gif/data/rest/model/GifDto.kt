package burrows.apps.example.gif.data.rest.model

import com.squareup.moshi.Json

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class GifDto {
  @Json(name = "url") var url: String? = null
  @Json(name = "preview") var preview: String? = null

  /**
   * No args constructor for use in serialization.
   */
  @Suppress("ConvertSecondaryConstructorToPrimary")
  constructor()
}
