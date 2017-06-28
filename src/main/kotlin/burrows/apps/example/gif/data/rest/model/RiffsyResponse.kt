package burrows.apps.example.gif.data.rest.model

import com.google.gson.annotations.SerializedName

/**
 * No args constructor for use in serialization.
 *
 * Riffsy Api Response.
 * eg. https://api.riffsy.com/v1/search?key=LIVDSRZULELA&tag=goodluck&limit=10
 *
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class RiffsyResponse {
  @SerializedName("results") var results: List<Result>? = null
  @SerializedName("page") var page: Float? = null
}
