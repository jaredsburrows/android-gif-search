package com.burrowsapps.gif.search.data.repository

import com.burrowsapps.gif.search.data.api.TenorService
import com.burrowsapps.gif.search.data.api.model.NetworkResult
import com.burrowsapps.gif.search.data.api.model.NetworkResult.Companion.safeApiCall
import com.burrowsapps.gif.search.data.api.model.TenorResponseDto
import javax.inject.Inject

internal class GifRepository @Inject internal constructor(
  private val service: TenorService,
) {
  suspend fun getSearchResults(query: String, position: String?): NetworkResult<TenorResponseDto> =
    safeApiCall { service.fetchSearchResults(query, position) }

  suspend fun getTrendingResults(position: String?): NetworkResult<TenorResponseDto> =
    safeApiCall { service.fetchTrendingResults(position) }
}
