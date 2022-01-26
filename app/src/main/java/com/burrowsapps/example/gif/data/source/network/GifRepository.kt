package com.burrowsapps.example.gif.data.source.network

import com.burrowsapps.example.gif.data.source.network.NetworkResult.Companion.safeApiCall
import com.burrowsapps.example.gif.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GifRepository @Inject constructor(
  private val service: TenorService,
  @IoDispatcher private val dispatcher: CoroutineDispatcher,
) {
  suspend fun getTrendingResults(pos: String?): Flow<NetworkResult<TenorResponseDto>> {
    return flow {
      emit(safeApiCall { service.getTrendingResults(pos) })
    }.flowOn(dispatcher)
  }

  suspend fun getSearchResults(q: String, pos: String?): Flow<NetworkResult<TenorResponseDto>> {
    return flow {
      emit(safeApiCall { service.getSearchResults(q, pos) })
    }.flowOn(dispatcher)
  }
}
