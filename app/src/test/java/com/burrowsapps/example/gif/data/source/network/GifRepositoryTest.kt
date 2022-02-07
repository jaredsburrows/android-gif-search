package com.burrowsapps.example.gif.data.source.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.example.gif.BuildConfig.BASE_URL
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol.HTTP_1_1
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR

@RunWith(AndroidJUnit4::class)
class GifRepositoryTest {
  @get:Rule(order = 0)
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  private val testDispatcher = UnconfinedTestDispatcher()
  private val service = mock<TenorService>()
  private val next = "0.0"
  private val response = TenorResponseDto()

  private lateinit var sut: GifRepository

  @Before
  fun setUp() {
    sut = GifRepository(service, testDispatcher)
  }

  @Test
  fun testLoadTrendingImagesSuccess() = runTest {
    whenever(service.getTrendingResults(eq(next)))
      .thenReturn(Response.success(response))

    val result = sut.getTrendingResults(next).first().data

    verify(service).getTrendingResults(eq(next))
    assertThat(result).isEqualTo(response)
  }

  @Test
  fun testLoadTrendingImagesError() = runTest {
    val errorResponse: okhttp3.Response = okhttp3.Response.Builder()
      .code(HTTP_INTERNAL_ERROR)
      .message("Broken!")
      .protocol(HTTP_1_1)
      .request(okhttp3.Request.Builder().url(BASE_URL).build())
      .build()
    val plainText = "text/plain; charset=utf-8".toMediaType()
    val errorBody = "Broken!".toResponseBody(plainText)
    whenever(service.getTrendingResults(eq(next)))
      .thenReturn(Response.error(errorBody, errorResponse))

    val result = sut.getTrendingResults(next).first().data

    verify(service).getTrendingResults(eq(next))
    assertThat(result).isNull()
  }

  @Test
  fun testLoadSearchImagesSuccess() = runTest {
    val searchString = "gifs"
    whenever(service.getSearchResults(eq(searchString), eq(next)))
      .thenReturn(Response.success(response))

    val result = sut.getSearchResults(searchString, next).first().data

    verify(service).getSearchResults(eq(searchString), eq(next))
    assertThat(result).isEqualTo(response)
  }

  @Test
  fun testLoadSearchImagesError() = runTest {
    val searchString = "gifs"
    val errorResponse: okhttp3.Response = okhttp3.Response.Builder()
      .code(HTTP_INTERNAL_ERROR)
      .message("Broken!")
      .protocol(HTTP_1_1)
      .request(okhttp3.Request.Builder().url(BASE_URL).build())
      .build()
    val plainText = "text/plain; charset=utf-8".toMediaType()
    val errorBody = "Broken!".toResponseBody(plainText)
    whenever(service.getSearchResults(eq(searchString), eq(next)))
      .thenReturn(Response.error(errorBody, errorResponse))

    val result = sut.getSearchResults(searchString, next).first().data

    verify(service).getSearchResults(eq(searchString), eq(next))
    assertThat(result).isNull()
  }
}
