package com.burrowsapps.example.gif.data.source.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.example.gif.data.source.network.TenorService.Companion.DEFAULT_LIMIT_COUNT
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class GifRepositoryTest {
  @get:Rule(order = 0)
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  private val testDispatcher = UnconfinedTestDispatcher()

  @Test
  fun testLoadTrendingImagesSuccess() = runTest {
    val next = 0.0
    val response = TenorResponseDto()
    val service: TenorService = mock()
    val sut = GifRepository(service, testDispatcher)
    whenever(service.getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Response.success(response))

    val result = sut.getTrendingResults(DEFAULT_LIMIT_COUNT, next).first()

    verify(service).getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next))
    assertThat(response).isEqualTo(result.data)
  }

  @Test
  fun testLoadSearchImagesSuccess() = runTest {
    val searchString = "gifs"
    val next = 0.0
    val response = TenorResponseDto()
    val service: TenorService = mock()
    val sut = GifRepository(service, testDispatcher)
    whenever(service.getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Response.success(response))

    val result = sut.getSearchResults(searchString, DEFAULT_LIMIT_COUNT, next).first()

    verify(service).getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next))
    assertThat(response).isEqualTo(result.data)
  }
}
