package com.burrowsapps.example.gif.data.source.network

import com.burrowsapps.example.gif.data.source.network.NetworkResult.Companion.safeApiCall
import javax.inject.Inject

class GifRepository @Inject internal constructor(
  private val service: TenorService,
) {
  suspend fun getTrendingResults(position: String?): NetworkResult<TenorResponseDto> =
    safeApiCall { service.getTrendingResults(position) }

  suspend fun getSearchResults(query: String, position: String?): NetworkResult<TenorResponseDto> =
    safeApiCall { service.getSearchResults(query, position) }
}
