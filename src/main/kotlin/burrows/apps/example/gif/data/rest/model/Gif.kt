package burrows.apps.example.gif.data.rest.model

import com.google.gson.annotations.SerializedName

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class Gif {
  @SerializedName("url") var url: String? = null
  @SerializedName("preview") var preview: String? = null

  /**
   * No args constructor for use in serialization.
   */
  @Suppress("ConvertSecondaryConstructorToPrimary")
  constructor()
}
