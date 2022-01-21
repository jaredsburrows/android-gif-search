package com.burrowsapps.example.gif.giflist

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.example.gif.data.remote.TenorService
import com.burrowsapps.example.gif.data.remote.TenorService.Companion.DEFAULT_LIMIT_COUNT
import com.burrowsapps.example.gif.model.TenorResponseDto
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response
import test.TestCoroutineDispatcherProvider

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class GifPresenterTest {
  private val dispatcherProvider = TestCoroutineDispatcherProvider()

  @Test
  fun testLoadTrendingImagesSuccess() = runTest {
    val next = 0.0
    val response = TenorResponseDto()
    val service: TenorService = mock()
    val sut = GifPresenter(service, dispatcherProvider)
    whenever(service.getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Response.success(response))

    sut.loadTrendingImages(next)

    verify(service).getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next))
  }

  @Test
  fun testLoadTrendingImageNotActive() = runTest {
    val next = 0.0
    val response = TenorResponseDto()
    val service: TenorService = mock()
    val sut = GifPresenter(service, dispatcherProvider)
    whenever(service.getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Response.success(response))

    sut.loadTrendingImages(next)

    verify(service, times(0)).getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next))
  }

  @Test
  fun testLoadSearchImagesSuccess() = runTest {
    val searchString = "gifs"
    val next = 0.0
    val response = TenorResponseDto()
    val service: TenorService = mock()
    val sut = GifPresenter(service, dispatcherProvider)

    whenever(service.getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Response.success(response))

    sut.loadSearchImages(searchString, next)

    verify(service).getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next))
  }

  @Test
  fun testLoadSearchImagesNotActive() = runTest {
    val searchString = "gifs"
    val next = 0.0
    val response = TenorResponseDto()
    val service: TenorService = mock()
    val sut = GifPresenter(service, dispatcherProvider)

    whenever(service.getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Response.success(response))

    sut.loadSearchImages(searchString, next)

    verify(service, times(0)).getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next))
  }
}
