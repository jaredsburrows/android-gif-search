package com.burrowsapps.example.gif.data

import com.burrowsapps.example.gif.data.model.RiffsyResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Riffsy Api Service for getting "trending" and "search" api getResults.
 *
 * Custom Api interfaces for the Riffsy Api.
 */
interface RiffsyApiClient {
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
   * @param pos Position of getResults.
   * @return Response of trending getResults.
   */
  @GET("/v1/trending?key=$API_KEY&safesearch=strict")
  suspend fun getTrendingResults(
    @Query("limit") limit: Int,
    @Query("pos") pos: Double?
  ): RiffsyResponseDto

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
   * @param tag Search string to find gifs.
   * @param limit Limit getResults.
   * @param pos Position of getResults.
   * @return Response of search getResults.
   */
  @GET("/v1/search?key=$API_KEY&safesearch=strict")
  suspend fun getSearchResults(
    @Query("tag") tag: String,
    @Query("limit") limit: Int,
    @Query("pos") pos: Double?
  ): RiffsyResponseDto

  companion object {
    const val DEFAULT_LIMIT_COUNT = 24
    private const val API_KEY = "LIVDSRZULELA"
  }
}
