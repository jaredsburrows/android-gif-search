package burrows.apps.example.gif.data.rest.repository

import burrows.apps.example.gif.data.rest.model.RiffsyResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Riffsy Api Service for getting "trending" and "search" api getResults.
 *
 * Custom Api interfaces for the Riffsy Api.
 *
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
interface RiffsyApiClient {
  companion object {
    /**
     * URL for Riffsy.
     */
    val BASE_URL = "https://api.riffsy.com/"

    /**
     * Riffsy public API key.
     */
    const val API_KEY = "LIVDSRZULELA"

    /**
     * Riffsy limit getResults.
     */
    const val DEFAULT_LIMIT_COUNT = 24
  }

  /**
   * Get trending gif getResults.
   *
   * URL: https://api.riffsy.com/
   * Path: /v1/trending
   * Query: limit
   * Query: key
   * Query: pos
   * eg. https://api.riffsy.com/v1/trending?key=LIVDSRZULELA&limit=10&pos=1
   *
   * @param limit Limit getResults.
   * @param pos   Position of getResults.
   * @return Response of trending getResults.
   */
  @GET("/v1/trending?key=" + API_KEY) fun getTrendingResults(@Query("limit") limit: Int,
                                                             @Query("pos") pos: Float?): Observable<RiffsyResponse> // Allow passing null

  /**
   * Get search gif getResults by a search string.
   *
   * URL: https://api.riffsy.com/
   * Path: /v1/search
   * Query: q
   * Query: limit
   * Query: key
   * Query: pos
   * eg. https://api.riffsy.com/v1/search?key=LIVDSRZULELA&tag=goodluck&limit=10&pos=1
   *
   * @param tag   Search string to find gifs.
   * @param limit Limit getResults.
   * @param pos   Position of getResults.
   * @return Response of search getResults.
   */
  @GET("/v1/search?key=" + API_KEY) fun getSearchResults(@Query("tag") tag: String?,
                                                         @Query("limit") limit: Int,
                                                         @Query("pos") pos: Float?): Observable<RiffsyResponse>  // Allow passing null
}
