package com.burrowsapps.example.gif.ui.giflist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.example.gif.data.source.network.GifRepository
import com.burrowsapps.example.gif.data.source.network.NetworkResult
import com.burrowsapps.example.gif.data.source.network.TenorResponseDto
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifViewModelTest {
  @get:Rule(order = 0)
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  private val repository = mock<GifRepository>()
  private val next = "0.0"
  private val response = TenorResponseDto()

  private lateinit var sut: GifViewModel

  @Before
  fun setUp() {
    sut = GifViewModel(repository)
  }

  @Test
  fun testLoadTrendingImagesSuccess() = runTest {
    val sut = GifViewModel(repository)
    whenever(repository.getTrendingResults(eq(next)))
      .thenReturn(flowOf(NetworkResult.Success(response)))

    sut.loadTrendingImages(next)
    val nextResult = sut.nextPageResponse.value
    val trendingResult = sut.trendingResponse.value

    verify(repository).getTrendingResults(eq(next))
    assertThat(nextResult).isEqualTo("0.0")
    assertThat(trendingResult).isEqualTo(listOf<TenorResponseDto>())
  }

  @Test
  fun testLoadTrendingImagesError() = runTest {
    val sut = GifViewModel(repository)
    whenever(repository.getTrendingResults(eq(next)))
      .thenReturn(flowOf(NetworkResult.Error(message = "Broken!")))

    sut.loadTrendingImages(next)
    val nextResult = sut.nextPageResponse.value
    val trendingResult = sut.trendingResponse.value

    verify(repository).getTrendingResults(eq(next))
    assertThat(nextResult).isNull()
    assertThat(trendingResult).isNull()
  }

  @Test
  fun testLoadSearchImagesSuccess() = runTest {
    val searchString = "gifs"
    val sut = GifViewModel(repository)
    whenever(repository.getSearchResults(eq(searchString), eq(next)))
      .thenReturn(flowOf(NetworkResult.Success(response)))

    sut.loadSearchImages(searchString, next)
    val nextResult = sut.nextPageResponse.value
    val searchResult = sut.searchResponse.value

    verify(repository).getSearchResults(eq(searchString), eq(next))
    assertThat(nextResult).isEqualTo("0.0")
    assertThat(searchResult).isEqualTo(listOf<TenorResponseDto>())
  }

  @Test
  fun testLoadSearchImagesError() = runTest {
    val searchString = "gifs"
    val sut = GifViewModel(repository)
    whenever(repository.getSearchResults(eq(searchString), eq(next)))
      .thenReturn(flowOf(NetworkResult.Error(message = "Broken!")))

    sut.loadSearchImages(searchString, next)
    val nextResult = sut.nextPageResponse.value
    val searchResult = sut.searchResponse.value

    verify(repository).getSearchResults(eq(searchString), eq(next))
    assertThat(nextResult).isNull()
    assertThat(searchResult).isNull()
  }
}
