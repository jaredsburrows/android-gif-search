package burrows.apps.example.gif.data.rest.model

import com.google.gson.annotations.SerializedName

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class Media {
  @SerializedName("tinygif") var gif: Gif? = null

  /**
   * No args constructor for use in serialization.
   */
  constructor()
}
