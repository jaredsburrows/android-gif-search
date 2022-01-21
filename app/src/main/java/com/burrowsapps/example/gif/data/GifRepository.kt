package com.burrowsapps.example.gif.data

import com.burrowsapps.example.gif.CoroutineDispatcherProvider
import com.burrowsapps.example.gif.data.remote.GifRemoteDataSource
import com.burrowsapps.example.gif.data.remote.NetworkResult
import com.burrowsapps.example.gif.data.remote.safeApiCall
import com.burrowsapps.example.gif.model.TenorResponseDto
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class GifRepository @Inject constructor(
  private val remoteDataSource: GifRemoteDataSource,
  private val dispatcher: CoroutineDispatcherProvider,
) {
  suspend fun getTrendingResults(limit: Int, pos: Double?): Flow<NetworkResult<TenorResponseDto>> {
    return flow {
      emit(safeApiCall { remoteDataSource.getTrendingResults(limit, pos) })
    }.flowOn(dispatcher.io())
  }

  suspend fun getSearchResults(
    q: String,
    limit: Int,
    pos: Double?
  ): Flow<NetworkResult<TenorResponseDto>> {
    return flow {
      emit(safeApiCall { remoteDataSource.getSearchResults(q, limit, pos) })
    }.flowOn(dispatcher.io())
  }
}
