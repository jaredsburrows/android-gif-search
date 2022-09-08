package com.burrowsapps.example.gif.ui.giflist

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.example.gif.data.source.network.GifRepository
import com.burrowsapps.example.gif.data.source.network.NetworkResult
import com.burrowsapps.example.gif.data.source.network.TenorResponseDto
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
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
  private val response = TenorResponseDto()

  private lateinit var sut: GifViewModel

  @Before
  fun setUp() {
    sut = GifViewModel(repository, testDispatcher)
  }

  @Test
  fun testLoadTrendingImagesSuccess() = runTest {
    whenever(repository.getTrendingResults(eq(next)))
      .thenReturn(NetworkResult.Success(response))

    sut.loadTrendingImages(next)
    val nextResult = sut.nextPageResponse.value
    val gifListResult = sut.gifListResponse.value

    verify(repository).getTrendingResults(eq(next))
    assertThat(nextResult).isEqualTo("0.0")
    assertThat(gifListResult).isEqualTo(listOf<TenorResponseDto>())
  }

  @Test
  fun testLoadTrendingImagesError() = runTest {
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
  fun testLoadSearchImagesSuccess() = runTest {
    val searchString = "gifs"
    whenever(repository.getSearchResults(eq(searchString), eq(next)))
      .thenReturn(NetworkResult.Success(response))

    sut.loadSearchImages(searchString, next)
    val nextResult = sut.nextPageResponse.value
    val gifListResult = sut.gifListResponse.value

    verify(repository).getSearchResults(eq(searchString), eq(next))
    assertThat(nextResult).isEqualTo("0.0")
    assertThat(gifListResult).isEqualTo(listOf<TenorResponseDto>())
  }

  @Test
  fun testLoadSearchImagesError() = runTest {
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
