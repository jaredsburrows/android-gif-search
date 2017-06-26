package burrows.apps.example.gif.data.rest.model

import com.google.gson.annotations.SerializedName

/**
 * No args constructor for use in serialization.
 *
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class Media {
  @SerializedName("tinygif") var gif: Gif? = null

  fun gif(): Gif? {
    return gif
  }
}
