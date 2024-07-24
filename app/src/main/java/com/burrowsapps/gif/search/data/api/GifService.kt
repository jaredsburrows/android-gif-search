package com.burrowsapps.gif.search.data.api

import com.burrowsapps.gif.search.data.api.model.GifResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Tenor Api Service for getting "trending" and "search" api results.
 */
internal interface GifService {
  /**
   * Get trending gif results.
   *
   * URL: https://g.tenor.com/
   * Path: /v1/trending
   * Query Parameters:
   * - key: REQUIRED, client key for privileged API access
   * - media_filter: STRONGLY RECOMMENDED, minimal - tinygif, gif, and mp4.
   * - limit: OPTIONAL, fetch up to a specified number of results (max: 50).
   * - pos: OPTIONAL, get results starting at position "value".
   *
   * Example: https://g.tenor.com/v1/trending?key=LIVDSRZULELA&media_filter=minimal&limit=45
   *
   * @param position Position of results.
   * @param apiKey Client key for privileged API access (default: API_KEY).
   * @param mediaFilter Media filter (default: MEDIA_FILTER).
   * @param limit Number of results to fetch (default: DEFAULT_LIMIT_COUNT).
   * @return Response of trending results.
   */
  @GET("/v1/trending")
  suspend fun fetchTrendingResults(
    @Query("pos") position: String?,
    @Query("key") apiKey: String = API_KEY,
    @Query("media_filter") mediaFilter: String = MEDIA_FILTER,
    @Query("limit") limit: Int = DEFAULT_LIMIT_COUNT,
  ): Response<GifResponseDto>

  /**
   * Get search gif results by a search string.
   *
   * URL: https://g.tenor.com/
   * Path: /v1/search
   * Query Parameters:
   * - key: REQUIRED, client key for privileged API access
   * - media_filter: STRONGLY RECOMMENDED, minimal - tinygif, gif, and mp4.
   * - q: REQUIRED, a search string
   * - limit: OPTIONAL, fetch up to a specified number of results (max: 50).
   * - pos: OPTIONAL, get results starting at position "value".
   *
   * Example: https://g.tenor.com/v1/search?key=LIVDSRZULELA&media_filter=minimal&q=hello&limit=45
   *
   * @param query Search string to find gifs.
   * @param position Position of results.
   * @param apiKey Client key for privileged API access (default: API_KEY).
   * @param mediaFilter Media filter (default: MEDIA_FILTER).
   * @param limit Number of results to fetch (default: DEFAULT_LIMIT_COUNT).
   * @return Response of search results.
   */
  @GET("/v1/search")
  suspend fun fetchSearchResults(
    @Query("q") query: String,
    @Query("pos") position: String?,
    @Query("key") apiKey: String = API_KEY,
    @Query("media_filter") mediaFilter: String = MEDIA_FILTER,
    @Query("limit") limit: Int = DEFAULT_LIMIT_COUNT,
  ): Response<GifResponseDto>

  companion object {
    private const val DEFAULT_LIMIT_COUNT = 15 * 3 // 3 for grid layout, 15 per page, 50 max
    private const val API_KEY = "LIVDSRZULELA"
    private const val MEDIA_FILTER = "minimal"
  }
}
