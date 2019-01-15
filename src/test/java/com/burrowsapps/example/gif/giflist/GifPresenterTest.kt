package com.burrowsapps.example.gif.giflist

import com.burrowsapps.example.gif.data.RiffsyApiClient
import com.burrowsapps.example.gif.data.RiffsyApiClient.Companion.DEFAULT_LIMIT_COUNT
import com.burrowsapps.example.gif.data.model.RiffsyResponseDto
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import test.TestImmediateSchedulerProvider

class GifPresenterTest {
  private val provider = TestImmediateSchedulerProvider()
  private var view: GifContract.View = mock()
  private var repository: RiffsyApiClient = mock()
  private lateinit var sut: GifPresenter

  @Before fun setUp() {
    whenever(view.isActive()).thenReturn(true)
  }

  @Test fun testLoadTrendingImageNotActive() {
    val next = 0.0
    val response = RiffsyResponseDto()
    whenever(view.isActive()).thenReturn(false)
    sut = GifPresenter(repository, provider)
    sut.takeView(view)
    whenever(repository.getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Observable.just(response))

    sut.loadTrendingImages(next)

    verify(view).isActive()
    verify(view, times(0)).addImages(eq(response))
  }

  @Test fun testLoadTrendingImagesSuccess() {
    val next = 0.0
    val response = RiffsyResponseDto()
    sut = GifPresenter(repository, provider)
    sut.takeView(view)
    whenever(repository.getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Observable.just(response))

    sut.loadTrendingImages(next)

    verify(view).isActive()
    verify(view).addImages(eq(response))
  }

  @Test fun testLoadSearchImagesSuccess() {
    val searchString = "gifs"
    val next = 0.0
    val response = RiffsyResponseDto()
    sut = GifPresenter(repository, provider)
    sut.takeView(view)
    whenever(repository.getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Observable.just(response))

    sut.loadSearchImages(searchString, next)

    verify(view).isActive()
    verify(view).addImages(eq(response))
  }

  @Test fun testLoadSearchImageViewInactive() {
    whenever(view.isActive()).thenReturn(false)
    val searchString = "gifs"
    val next = 0.0
    val response = RiffsyResponseDto()
    sut = GifPresenter(repository, provider)
    sut.takeView(view)
    whenever(repository.getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Observable.just(response))

    sut.loadSearchImages(searchString, next)

    verify(view, never()).addImages(any())
  }
}
