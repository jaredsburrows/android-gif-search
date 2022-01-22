package com.burrowsapps.example.gif.data.source.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Tenor Api Service for getting "trending" and "search" api getResults.
 *
 * Custom Api interfaces for the Tenor Api.
 */
interface TenorService {
  /**
   * Get trending gif results.
   *
   * URL: https://g.tenor.com/
   * Path: /v1/trending
   * Query: key REQUIRED, client key for privileged API access
   * Query: limit OPTIONAL, fetch up to a specified number of results (max: 50).
   * Query: pos OPTIONAL, get results starting at position "value".
   * eg. https://g.tenor.com/v1/trending?key=LIVDSRZULELA&limit=10&pos=1
   *
   * @param limit Limit getResults.
   * @param pos Position of getResults.
   * @return Response of trending getResults.
   */
  @GET("/v1/trending?key=$API_KEY")
  suspend fun getTrendingResults(
    @Query("limit")
    limit: Int,
    @Query("pos")
    pos: Double?
  ): Response<TenorResponseDto>

  /**
   * Get search gif results by a search string.
   *
   * URL: https://g.tenor.com/
   * Path: /v1/search
   * Query: key REQUIRED, client key for privileged API access
   * Query: q REQUIRED, a search string
   * Query: limit OPTIONAL, fetch up to a specified number of results (max: 50).
   * Query: pos OPTIONAL, get results starting at position "value".
   * eg. https://g.tenor.com/v1/search?key=LIVDSRZULELA&q=goodluck&limit=10&pos=1
   *
   * @param q Search string to find gifs.
   * @param limit Limit getResults.
   * @param pos Position of getResults.
   * @return Response of search getResults.
   */
  @GET("/v1/search?key=$API_KEY")
  suspend fun getSearchResults(
    @Query("q")
    q: String,
    @Query("limit")
    limit: Int,
    @Query("pos")
    pos: Double?
  ): Response<TenorResponseDto>

  companion object {
    const val DEFAULT_LIMIT_COUNT = 50
    private const val API_KEY = "LIVDSRZULELA"
  }
}
