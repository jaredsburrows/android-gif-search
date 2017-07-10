package burrows.apps.example.gif.data.rest.model

import com.squareup.moshi.Json

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class MediaDto {
  @field:Json(name = "tinygif") var gif: GifDto? = null // Bug in 1.5.0

  /**
   * No args constructor for use in serialization.
   */
  @Suppress("ConvertSecondaryConstructorToPrimary")
  constructor()
}
