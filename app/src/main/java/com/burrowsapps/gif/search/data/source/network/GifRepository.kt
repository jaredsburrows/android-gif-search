package com.burrowsapps.gif.search.data.source.network

import com.burrowsapps.gif.search.data.source.network.NetworkResult.Companion.safeApiCall
import javax.inject.Inject

internal class GifRepository @Inject internal constructor(
  private val service: TenorService,
) {
  suspend fun getSearchResults(query: String, position: String?): NetworkResult<TenorResponseDto> =
    safeApiCall { service.getSearchResults(query, position) }

  suspend fun getTrendingResults(position: String?): NetworkResult<TenorResponseDto> =
    safeApiCall { service.getTrendingResults(position) }
}
