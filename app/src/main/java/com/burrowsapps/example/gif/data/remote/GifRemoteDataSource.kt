package com.burrowsapps.example.gif.data.remote

import com.burrowsapps.example.gif.model.TenorResponseDto
import retrofit2.Response
import javax.inject.Inject

class GifRemoteDataSource @Inject constructor(private val service: TenorService) {
  suspend fun getTrendingResults(limit: Int, pos: Double?): Response<TenorResponseDto> {
    return service.getTrendingResults(limit, pos)
  }

  suspend fun getSearchResults(
    q: String,
    limit: Int,
    pos: Double?
  ): Response<TenorResponseDto> {
    return service.getSearchResults(q, limit, pos)
  }
}
