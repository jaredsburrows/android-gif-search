@file:OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)

package com.burrowsapps.gif.search.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.data.api.model.GifDto
import com.burrowsapps.gif.search.data.api.model.GifResponseDto
import com.burrowsapps.gif.search.data.api.model.MediaDto
import com.burrowsapps.gif.search.data.api.model.NetworkResult
import com.burrowsapps.gif.search.data.api.model.ResultDto
import com.burrowsapps.gif.search.data.db.AppDatabase
import com.burrowsapps.gif.search.ui.giflist.GifImageInfo
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class GifRemoteMediatorTest {
  private lateinit var db: AppDatabase
  private val repository = mock<GifRepository>()
  private val dispatcher = UnconfinedTestDispatcher()

  @Before
  fun setUp() {
    db =
      Room
        .inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()
  }

  @After
  fun tearDown() {
    db.close()
  }

  private fun response(
    items: Int,
    prefix: String,
    next: String,
  ): GifResponseDto {
    val results =
      (1..items).map { i ->
        val gif = GifDto(url = "https://ex.com/${prefix}g$i.gif", preview = "https://ex.com/${prefix}gp$i.gif")
        val tiny = GifDto(url = "https://ex.com/${prefix}t$i.gif", preview = "https://ex.com/${prefix}tp$i.gif")
        val media = MediaDto(gif = gif, tinyGif = tiny)
        ResultDto(media = listOf(media))
      }
    return GifResponseDto(results = results, next = next)
  }

  private fun emptyState(): PagingState<Int, GifImageInfo> =
    PagingState(pages = emptyList(), anchorPosition = null, config = PagingConfig(pageSize = 45), leadingPlaceholderCount = 0)

  @Test
  fun refresh_trending_storesItemsAndKeys() =
    runTest(dispatcher) {
      whenever(repository.getTrendingResults(anyOrNull())).thenReturn(NetworkResult.Success(response(2, "a", next = "10.0")))

      val mediator = GifRemoteMediator(queryKey = "", repository = repository, database = db, dispatcher = dispatcher)
      val result = mediator.load(LoadType.REFRESH, emptyState())
      assertThat(result).isInstanceOf(androidx.paging.RemoteMediator.MediatorResult.Success::class.java)

      val list = db.queryResultDao().allForQuery("")
      assertThat(list).hasSize(2)
      assertThat(db.remoteKeysDao().remoteKeys("")?.nextKey).isEqualTo("10.0")
    }

  @Test
  fun append_trending_appendsAndUpdatesKey() =
    runTest(dispatcher) {
      whenever(repository.getTrendingResults(anyOrNull()))
        .thenReturn(NetworkResult.Success(response(2, "a", next = "10.0")))
        .thenReturn(NetworkResult.Success(response(2, "b", next = "20.0")))

      val mediator = GifRemoteMediator(queryKey = "", repository = repository, database = db, dispatcher = dispatcher)
      mediator.load(LoadType.REFRESH, emptyState())
      val result = mediator.load(LoadType.APPEND, emptyState())
      assertThat(result).isInstanceOf(androidx.paging.RemoteMediator.MediatorResult.Success::class.java)

      val list = db.queryResultDao().allForQuery("")
      assertThat(list).hasSize(4)
      assertThat(list.first().tinyGifUrl).contains("a")
      assertThat(list.last().tinyGifUrl).contains("b")
      assertThat(db.remoteKeysDao().remoteKeys("")?.nextKey).isEqualTo("20.0")
    }

  @Test
  fun error_returnsError() =
    runTest(dispatcher) {
      whenever(repository.getTrendingResults(anyOrNull())).thenReturn(NetworkResult.Error<GifResponseDto>(message = "boom"))
      val mediator = GifRemoteMediator(queryKey = "", repository = repository, database = db, dispatcher = dispatcher)
      val result = mediator.load(LoadType.REFRESH, emptyState())
      assertThat(result).isInstanceOf(androidx.paging.RemoteMediator.MediatorResult.Error::class.java)
    }
}
