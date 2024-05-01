package com.burrowsapps.gif.search.data.repository

import com.burrowsapps.gif.search.data.api.GifService
import com.burrowsapps.gif.search.data.api.model.GifResponseDto
import com.burrowsapps.gif.search.data.api.model.NetworkResult
import com.burrowsapps.gif.search.data.api.model.NetworkResult.Companion.safeApiCall
import javax.inject.Inject

internal class GifRepository
  @Inject
  internal constructor(
    private val service: GifService,
  ) {
    suspend fun getSearchResults(
      query: String,
      position: String?,
    ): NetworkResult<GifResponseDto> = safeApiCall { service.fetchSearchResults(query, position) }

    suspend fun getTrendingResults(position: String?): NetworkResult<GifResponseDto> =
      safeApiCall { service.fetchTrendingResults(position) }
  }
