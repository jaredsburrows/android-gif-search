package burrows.apps.example.gif.data.rest.repository

import burrows.apps.example.gif.data.rest.model.RiffsyResponseDto
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
    const val API_KEY = "LIVDSRZULELA"
    val DEFAULT_LIMIT_COUNT = 24
  }

  /**
   * Get trending gif results.
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
                                                             @Query("pos") pos: Int?): Observable<RiffsyResponseDto> // Allow passing null

  /**
   * Get search gif results by a search string.
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
  @GET("/v1/search?key=" + API_KEY) fun getSearchResults(@Query("tag") tag: String,
                                                         @Query("limit") limit: Int,
                                                         @Query("pos") pos: Int?): Observable<RiffsyResponseDto>  // Allow passing null
}
