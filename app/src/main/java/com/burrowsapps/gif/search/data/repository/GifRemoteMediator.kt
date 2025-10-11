package com.burrowsapps.gif.search.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.burrowsapps.gif.search.data.api.model.GifResponseDto
import com.burrowsapps.gif.search.data.api.model.NetworkResult
import com.burrowsapps.gif.search.data.db.AppDatabase
import com.burrowsapps.gif.search.data.db.entity.GifEntity
import com.burrowsapps.gif.search.data.db.entity.QueryResultEntity
import com.burrowsapps.gif.search.data.db.entity.RemoteKeysEntity
import com.burrowsapps.gif.search.ui.giflist.GifImageInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
internal class GifRemoteMediator(
  private val queryKey: String,
  private val repository: GifRepository,
  private val database: AppDatabase,
  private val dispatcher: CoroutineDispatcher,
) : RemoteMediator<Int, GifImageInfo>() {
  override suspend fun load(
    loadType: LoadType,
    state: PagingState<Int, GifImageInfo>,
  ): MediatorResult {
    val remoteKeysDao = database.remoteKeysDao()
    val gifDao = database.gifDao()
    val queryResultsDao = database.queryResultDao()

    val currentKey =
      when (loadType) {
        LoadType.REFRESH -> null
        LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
        LoadType.APPEND -> remoteKeysDao.remoteKeys(queryKey)?.nextKey
      }

    val result: NetworkResult<GifResponseDto> =
      withContext(dispatcher) {
        if (queryKey.isBlank()) {
          repository.getTrendingResults(currentKey)
        } else {
          repository.getSearchResults(queryKey, currentKey)
        }
      }

    return when (result) {
      is NetworkResult.Error -> MediatorResult.Error(Exception(result.message))
      is NetworkResult.Loading -> MediatorResult.Error(IllegalStateException("Unexpected Loading"))
      is NetworkResult.Empty -> MediatorResult.Success(endOfPaginationReached = true)
      is NetworkResult.Success -> {
        val response = result.data
        val items = buildGifList(response)

        database.withTransaction {
          if (loadType == LoadType.REFRESH) {
            queryResultsDao.clearQuery(queryKey)
            remoteKeysDao.clearQuery(queryKey)
          }

          // upsert gifs
          val gifEntities =
            items.map { info ->
              GifEntity(
                tinyGifUrl = info.tinyGifUrl,
                tinyGifPreviewUrl = info.tinyGifPreviewUrl,
                gifUrl = info.gifUrl,
                gifPreviewUrl = info.gifPreviewUrl,
              )
            }
          gifDao.upsertAll(gifEntities)

          // compute start position
          val startPos = if (loadType == LoadType.APPEND) queryResultsDao.nextPositionForQuery(queryKey) else 0L

          val mappings =
            items.mapIndexed { index, item ->
              QueryResultEntity(searchKey = queryKey, gifId = item.tinyGifUrl, position = startPos + index)
            }
          queryResultsDao.insertAll(mappings)

          // update remote keys
          val nextKey = response?.next?.takeUnless { it == currentKey }
          remoteKeysDao.upsert(RemoteKeysEntity(searchKey = queryKey, nextKey = nextKey))
        }

        MediatorResult.Success(endOfPaginationReached = items.isEmpty())
      }
    }
  }

  private fun buildGifList(response: GifResponseDto?): List<GifImageInfo> =
    response
      ?.results
      ?.map { result ->
        val media = result.media.firstOrNull()
        val gifUrl = media?.gif?.url.orEmpty()
        val gifPreviewUrl = media?.gif?.preview.orEmpty()
        val tinyUrl = media?.tinyGif?.url.orEmpty()
        val tinyPreview = media?.tinyGif?.preview.orEmpty()

        GifImageInfo(
          gifUrl = gifUrl,
          gifPreviewUrl = gifPreviewUrl,
          tinyGifUrl = tinyUrl,
          tinyGifPreviewUrl = tinyPreview,
        )
      }.orEmpty()
}
