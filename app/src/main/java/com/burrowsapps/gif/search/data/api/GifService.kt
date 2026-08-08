package com.burrowsapps.gif.search.data.api

import com.burrowsapps.gif.search.data.api.model.GifResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Klipy Api Service for getting "trending" and "search" api results.
 *
 * https://docs.klipy.com/
 */
internal interface GifService {
  /**
   * Get trending gif results.
   *
   * URL: https://api.klipy.com/
   * Path: /api/v1/{app_key}/gifs/trending
   * Query Parameters:
   * - page: OPTIONAL, page number to fetch (starts at 1).
   * - per_page: OPTIONAL, number of results per page.
   *
   * Example: https://api.klipy.com/api/v1/{app_key}/gifs/trending?page=1&per_page=45
   *
   * @param page Page number to fetch (starts at 1).
   * @param perPage Number of results per page (default: DEFAULT_LIMIT_COUNT).
   * @return Response of trending results.
   */
  @GET("/api/v1/$API_KEY/gifs/trending")
  suspend fun fetchTrendingResults(
    @Query("page") page: Int,
    @Query("per_page") perPage: Int = DEFAULT_LIMIT_COUNT,
  ): Response<GifResponseDto>

  /**
   * Get search gif results by a search string.
   *
   * URL: https://api.klipy.com/
   * Path: /api/v1/{app_key}/gifs/search
   * Query Parameters:
   * - q: REQUIRED, a search string.
   * - page: OPTIONAL, page number to fetch (starts at 1).
   * - per_page: OPTIONAL, number of results per page.
   *
   * Example: https://api.klipy.com/api/v1/{app_key}/gifs/search?q=hello&page=1&per_page=45
   *
   * @param query Search string to find gifs.
   * @param page Page number to fetch (starts at 1).
   * @param perPage Number of results per page (default: DEFAULT_LIMIT_COUNT).
   * @return Response of search results.
   */
  @GET("/api/v1/$API_KEY/gifs/search")
  suspend fun fetchSearchResults(
    @Query("q") query: String,
    @Query("page") page: Int,
    @Query("per_page") perPage: Int = DEFAULT_LIMIT_COUNT,
  ): Response<GifResponseDto>

  companion object {
    private const val DEFAULT_LIMIT_COUNT = 15 * 3 // 3 for grid layout, 15 per page

    // The app key is embedded in the request path, per https://docs.klipy.com/.
    private const val API_KEY = "bA9nOCYnCLJhSswsDyJQcGMiigKFLuh2WaZRjzZF8ZfTDxLIrph5CGcHspLqZo9n"
  }
}
