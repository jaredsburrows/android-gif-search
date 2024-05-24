@file:OptIn(ExperimentalCoroutinesApi::class)

package com.burrowsapps.gif.search.ui.giflist

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.data.api.model.GifResponseDto
import com.burrowsapps.gif.search.data.api.model.NetworkResult
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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifViewModelTest {
  private val testDispatcher = UnconfinedTestDispatcher()
  private val repository = mock<GifRepository>()
  private val next = "0.0"
  private val response = GifResponseDto()

  private lateinit var sut: GifViewModel

  @Before
  fun setUp() {
    sut = GifViewModel(repository, testDispatcher)
  }

  @Test
  fun testLoadTrendingImagesLoading() =
    runTest {
      whenever(repository.getTrendingResults(anyOrNull()))
        .thenReturn(NetworkResult.Loading())

      sut.loadTrendingImages(next)
      val nextResult = sut.nextPageResponse.value
      val gifListResult = sut.gifListResponse.value

      verify(repository).getTrendingResults(anyOrNull())
      assertThat(nextResult).isEmpty()
      assertThat(gifListResult).isEqualTo(listOf<GifResponseDto>())
    }

  @Test
  fun testLoadTrendingImagesSuccess() =
    runTest {
      whenever(repository.getTrendingResults(eq(next)))
        .thenReturn(NetworkResult.Success(response))

      sut.loadTrendingImages(next)
      val nextResult = sut.nextPageResponse.value
      val gifListResult = sut.gifListResponse.value

      verify(repository).getTrendingResults(eq(next))
      assertThat(nextResult).isEqualTo("0.0")
      assertThat(gifListResult).isEqualTo(listOf<GifResponseDto>())
    }

  @Test
  fun testLoadTrendingImagesEmpty() =
    runTest {
      whenever(repository.getTrendingResults(eq(next)))
        .thenReturn(NetworkResult.Empty())

      sut.loadTrendingImages(next)
      val nextResult = sut.nextPageResponse.value
      val gifListResult = sut.gifListResponse.value

      verify(repository).getTrendingResults(eq(next))
      assertThat(nextResult).isEmpty()
      assertThat(gifListResult).isEqualTo(listOf<GifResponseDto>())
    }

  @Test
  fun testLoadTrendingImagesError() =
    runTest {
      whenever(repository.getTrendingResults(eq(next)))
        .thenReturn(NetworkResult.Error(message = "Broken!"))

      sut.loadTrendingImages(next)
      val nextResult = sut.nextPageResponse.value
      val gifListResult = sut.gifListResponse.value

      verify(repository).getTrendingResults(eq(next))
      assertThat(nextResult).isEmpty()
      assertThat(gifListResult).isEmpty()
    }

  @Test
  fun testLoadSearchImagesLoading() =
    runTest {
      val searchString = "gifs"
      whenever(repository.getSearchResults(eq(searchString), anyOrNull()))
        .thenReturn(NetworkResult.Loading())

      sut.loadSearchImages(searchString, next)
      val nextResult = sut.nextPageResponse.value
      val gifListResult = sut.gifListResponse.value

      verify(repository).getSearchResults(eq(searchString), anyOrNull())
      assertThat(nextResult).isEmpty()
      assertThat(gifListResult).isEqualTo(listOf<GifResponseDto>())
    }

  @Test
  fun testLoadSearchImagesSuccess() =
    runTest {
      val searchString = "gifs"
      whenever(repository.getSearchResults(eq(searchString), eq(next)))
        .thenReturn(NetworkResult.Success(response))

      sut.loadSearchImages(searchString, next)
      val nextResult = sut.nextPageResponse.value
      val gifListResult = sut.gifListResponse.value

      verify(repository).getSearchResults(eq(searchString), eq(next))
      assertThat(nextResult).isEqualTo("0.0")
      assertThat(gifListResult).isEqualTo(listOf<GifResponseDto>())
    }

  @Test
  fun testLoadSearchImagesEmpty() =
    runTest {
      val searchString = "gifs"
      whenever(repository.getSearchResults(eq(searchString), eq(next)))
        .thenReturn(NetworkResult.Empty())

      sut.loadSearchImages(searchString, next)
      val nextResult = sut.nextPageResponse.value
      val gifListResult = sut.gifListResponse.value

      verify(repository).getSearchResults(eq(searchString), eq(next))
      assertThat(nextResult).isEmpty()
      assertThat(gifListResult).isEqualTo(listOf<GifResponseDto>())
    }

  @Test
  fun testLoadSearchImagesError() =
    runTest {
      val searchString = "gifs"
      whenever(repository.getSearchResults(eq(searchString), eq(next)))
        .thenReturn(NetworkResult.Error(message = "Broken!"))

      sut.loadSearchImages(searchString, next)
      val nextResult = sut.nextPageResponse.value
      val gifListResult = sut.gifListResponse.value

      verify(repository).getSearchResults(eq(searchString), eq(next))
      assertThat(nextResult).isEmpty()
      assertThat(gifListResult).isEmpty()
    }
}
