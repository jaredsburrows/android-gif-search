package burrows.apps.example.gif.data.rest.model

import com.squareup.moshi.Json

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class Media {
  @field:Json(name = "tinygif") var gif: Gif? = null // Bug in 1.5.0

  /**
   * No args constructor for use in serialization.
   */
  @Suppress("ConvertSecondaryConstructorToPrimary")
  constructor()
}
