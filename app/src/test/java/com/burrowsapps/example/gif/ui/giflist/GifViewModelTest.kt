package com.burrowsapps.example.gif.ui.giflist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.example.gif.data.source.network.GifRepository
import com.burrowsapps.example.gif.data.source.network.NetworkResult.Success
import com.burrowsapps.example.gif.data.source.network.TenorResponseDto
import com.burrowsapps.example.gif.data.source.network.TenorService.Companion.DEFAULT_LIMIT_COUNT
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
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
    whenever(repository.getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(flowOf(Success(response)))

    sut.loadTrendingImages(next)

    verify(repository).getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next))
  }

  @Test
  fun testLoadSearchImagesSuccess() = runTest {
    val searchString = "gifs"
    val sut = GifViewModel(repository)
    whenever(repository.getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(flowOf(Success(response)))

    sut.loadSearchImages(searchString, next)

    verify(repository).getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next))
  }
}
