package com.burrowsapps.example.gif.giflist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.example.gif.data.TenorService
import com.burrowsapps.example.gif.data.TenorService.Companion.DEFAULT_LIMIT_COUNT
import com.burrowsapps.example.gif.data.model.TenorResponseDto
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class GifPresenterTest {
  @get:Rule(order = 0)
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  private val testDispatcher = UnconfinedTestDispatcher()

  @Test
  fun testLoadTrendingImagesSuccess() = runTest {
    val next = 0.0
    val response = TenorResponseDto()
    val view: GifContract.View = mock()
    val service: TenorService = mock()
    val sut = GifPresenter(service, testDispatcher)
    whenever(view.isActive()).thenReturn(true)
    whenever(service.getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Response.success(response))

    sut.takeView(view)
    sut.loadTrendingImages(next)

    verify(view).isActive()
    verify(service).getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next))
    verify(view).addImages(eq(response))
  }

  @Test
  fun testLoadTrendingImageNotActive() = runTest {
    val next = 0.0
    val response = TenorResponseDto()
    val view: GifContract.View = mock()
    val service: TenorService = mock()
    val sut = GifPresenter(service, testDispatcher)
    whenever(view.isActive()).thenReturn(false)
    whenever(service.getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Response.success(response))

    sut.takeView(view)
    sut.loadTrendingImages(next)

    verify(view).isActive()
    verify(service, times(0)).getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next))
    verify(view, times(0)).addImages(eq(response))
  }

  @Test
  fun testLoadSearchImagesSuccess() = runTest {
    val searchString = "gifs"
    val next = 0.0
    val response = TenorResponseDto()
    val view: GifContract.View = mock()
    val service: TenorService = mock()
    val sut = GifPresenter(service, testDispatcher)

    whenever(view.isActive()).thenReturn(true)
    whenever(service.getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Response.success(response))

    sut.takeView(view)
    sut.loadSearchImages(searchString, next)

    verify(view).isActive()
    verify(service).getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next))
    verify(view).addImages(eq(response))
  }

  @Test
  fun testLoadSearchImagesNotActive() = runTest {
    val searchString = "gifs"
    val next = 0.0
    val response = TenorResponseDto()
    val view: GifContract.View = mock()
    val service: TenorService = mock()
    val sut = GifPresenter(service, testDispatcher)

    whenever(view.isActive()).thenReturn(false)
    whenever(service.getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Response.success(response))

    sut.takeView(view)
    sut.loadSearchImages(searchString, next)

    verify(view).isActive()
    verify(service, times(0)).getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next))
    verify(view, times(0)).addImages(eq(response))
  }
}
