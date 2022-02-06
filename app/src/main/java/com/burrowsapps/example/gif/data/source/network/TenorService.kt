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
   * Get search gif results by a search string.
   *
   * URL: https://g.tenor.com/
   * Path: /v1/search
   * Query: key REQUIRED, client key for privileged API access
   * Query: media_filter STRONGLY RECOMMENDED, minimal - tinygif, gif, and mp4.
   * Query: q REQUIRED, a search string
   * Query: limit OPTIONAL, fetch up to a specified number of results (max: 50).
   * Query: pos OPTIONAL, get results starting at position "value".
   * eg. https://g.tenor.com/v1/search?key=LIVDSRZULELA&media_filter=minimal&q=hello&limit=48
   *
   * @param q Search string to find gifs.
   * @param pos Position of getResults.
   * @return Response of search getResults.
   */
  @GET("/v1/search?key=$API_KEY&media_filter=$MEDIA_FILTER&limit=$DEFAULT_LIMIT_COUNT")
  suspend fun getSearchResults(
    @Query("q")
    q: String,
    @Query("pos")
    pos: String?
  ): Response<TenorResponseDto>

  /**
   * Get trending gif results.
   *
   * URL: https://g.tenor.com/
   * Path: /v1/trending
   * Query: key REQUIRED, client key for privileged API access
   * Query: media_filter STRONGLY RECOMMENDED, minimal - tinygif, gif, and mp4.
   * Query: limit OPTIONAL, fetch up to a specified number of results (max: 50).
   * Query: pos OPTIONAL, get results starting at position "value".
   * eg. https://g.tenor.com/v1/trending?key=LIVDSRZULELA&media_filter=minimal&limit=48
   *
   * @param pos Position of getResults.
   * @return Response of trending getResults.
   */
  @GET("/v1/trending?key=$API_KEY&media_filter=$MEDIA_FILTER&limit=$DEFAULT_LIMIT_COUNT")
  suspend fun getTrendingResults(
    @Query("pos")
    pos: String?
  ): Response<TenorResponseDto>

  companion object {
    const val DEFAULT_LIMIT_COUNT = 48 // multiple of 3 for gridlayout
    private const val API_KEY = "LIVDSRZULELA"
    private const val MEDIA_FILTER = "minimal"
  }
}
