package burrows.apps.example.gif.data.rest.model

import com.squareup.moshi.Json

/**
 * Riffsy Api Response.
 * eg. https://api.riffsy.com/v1/search?key=LIVDSRZULELA&tag=goodluck&limit=10
 *
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class RiffsyResponse {
  @Json(name = "results") var results: List<Result>? = null
  @Json(name = "page") var page: Float? = null

  /**
   * No args constructor for use in serialization.
   */
  @Suppress("ConvertSecondaryConstructorToPrimary")
  constructor()
}
