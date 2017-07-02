package burrows.apps.example.gif.data.rest.model

import com.google.gson.annotations.SerializedName

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class Result {
  @SerializedName("media") var media: List<Media>? = null
  @SerializedName("title") var title: String? = null

  /**
   * No args constructor for use in serialization.
   */
  @Suppress("ConvertSecondaryConstructorToPrimary")
  constructor()
}
