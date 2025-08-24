@file:OptIn(ExperimentalCoroutinesApi::class)

package com.burrowsapps.gif.search.ui.giflist

import androidx.paging.PagingSource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.data.api.model.GifDto
import com.burrowsapps.gif.search.data.api.model.GifResponseDto
import com.burrowsapps.gif.search.data.api.model.MediaDto
import com.burrowsapps.gif.search.data.api.model.NetworkResult
import com.burrowsapps.gif.search.data.api.model.ResultDto
import com.burrowsapps.gif.search.data.repository.GifRepository
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifPagingSourceTest {
  private val repository = mock<GifRepository>()
  private val dispatcher = UnconfinedTestDispatcher()

  private fun oneItemResponse(next: String = "42.0"): GifResponseDto {
    val gif = GifDto(url = "https://example.com/g.gif", preview = "https://example.com/gp.gif")
    val tiny = GifDto(url = "https://example.com/t.gif", preview = "https://example.com/tp.gif")
    val media = MediaDto(gif = gif, tinyGif = tiny)
    val result = ResultDto(media = listOf(media))
    return GifResponseDto(results = listOf(result), next = next)
  }

  @Test
  fun load_trending_success_mapsItemsAndNextKey() =
    runTest {
      whenever(repository.getTrendingResults(anyOrNull())).thenReturn(
        NetworkResult.Success(oneItemResponse(next = "100.0")),
      )

      val pagingSource = GifPagingSource(repository, dispatcher, query = null)
      val params =
        PagingSource.LoadParams.Refresh<String>(
          key = null,
          loadSize = 45,
          placeholdersEnabled = false,
        )

      val result = pagingSource.load(params)

      assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
      val page = result as PagingSource.LoadResult.Page
      assertThat(page.nextKey).isEqualTo("100.0")
      assertThat(page.prevKey).isNull()
      assertThat(page.data).hasSize(1)
      val item = page.data.first()
      assertThat(item.gifUrl).isEqualTo("https://example.com/g.gif")
      assertThat(item.gifPreviewUrl).isEqualTo("https://example.com/gp.gif")
      assertThat(item.tinyGifUrl).isEqualTo("https://example.com/t.gif")
      assertThat(item.tinyGifPreviewUrl).isEqualTo("https://example.com/tp.gif")

      verify(repository).getTrendingResults(anyOrNull())
    }

  @Test
  fun load_search_success_usesQuery() =
    runTest {
      val query = "cats"
      whenever(repository.getSearchResults(eq(query), anyOrNull())).thenReturn(
        NetworkResult.Success(oneItemResponse(next = "1.1")),
      )

      val pagingSource = GifPagingSource(repository, dispatcher, query = query)
      val params =
        PagingSource.LoadParams.Refresh<String>(
          key = null,
          loadSize = 45,
          placeholdersEnabled = false,
        )

      val result = pagingSource.load(params)

      assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
      val page = result as PagingSource.LoadResult.Page
      assertThat(page.nextKey).isEqualTo("1.1")
      assertThat(page.data).hasSize(1)
      verify(repository).getSearchResults(eq(query), anyOrNull())
    }

  @Test
  fun load_empty_returnsEmptyPage() =
    runTest {
      whenever(repository.getTrendingResults(anyOrNull())).thenReturn(NetworkResult.Empty())

      val pagingSource = GifPagingSource(repository, dispatcher, query = null)
      val params =
        PagingSource.LoadParams.Refresh<String>(
          key = null,
          loadSize = 45,
          placeholdersEnabled = false,
        )

      val result = pagingSource.load(params)

      assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
      val page = result as PagingSource.LoadResult.Page
      assertThat(page.data).isEmpty()
      assertThat(page.nextKey).isNull()
    }

  @Test
  fun load_error_emitsLoadResultError() =
    runTest {
      whenever(repository.getTrendingResults(anyOrNull())).thenReturn(
        NetworkResult.Error(message = "Boom"),
      )

      val pagingSource = GifPagingSource(repository, dispatcher, query = null)
      val params =
        PagingSource.LoadParams.Refresh<String>(
          key = null,
          loadSize = 45,
          placeholdersEnabled = false,
        )

      val result = pagingSource.load(params)

      assertThat(result).isInstanceOf(PagingSource.LoadResult.Error::class.java)
    }

  @Test
  fun load_loading_returnsEmptyPageWithSameKey() =
    runTest {
      whenever(repository.getTrendingResults(eq("10.0"))).thenReturn(
        NetworkResult.Loading(),
      )

      val pagingSource = GifPagingSource(repository, dispatcher, query = null)
      val params =
        PagingSource.LoadParams.Append<String>(
          key = "10.0",
          loadSize = 45,
          placeholdersEnabled = false,
        )

      val result = pagingSource.load(params)

      assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
      val page = result as PagingSource.LoadResult.Page
      assertThat(page.data).isEmpty()
      assertThat(page.nextKey).isEqualTo("10.0")
    }

  @Test
  fun getRefreshKey_alwaysNullForCursorPaging() {
    val pagingSource = GifPagingSource(repository, dispatcher, query = null)
    val refreshKey =
      pagingSource.getRefreshKey(
        androidx.paging.PagingState(
          pages = emptyList(),
          anchorPosition = null,
          config = androidx.paging.PagingConfig(pageSize = 45),
          leadingPlaceholderCount = 0,
        ),
      )

    assertThat(refreshKey).isNull()
  }
}
