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
    ): NetworkResult<GifResponseDto> = safeApiCall { service.fetchSearchResults(query, position.toPage()) }

    suspend fun getTrendingResults(position: String?): NetworkResult<GifResponseDto> =
      safeApiCall { service.fetchTrendingResults(position.toPage()) }

    private companion object {
      const val FIRST_PAGE = 1

      /** Klipy pagination is page-based; a null/invalid stored position means "load page 1". */
      fun String?.toPage(): Int = this?.toIntOrNull() ?: FIRST_PAGE
    }
  }
