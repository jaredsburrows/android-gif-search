package com.burrowsapps.gif.search.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.data.api.TenorService
import com.burrowsapps.gif.search.data.api.model.TenorResponseDto
import com.burrowsapps.gif.search.di.TestApiConfigModule
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol.HTTP_1_1
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR

@RunWith(AndroidJUnit4::class)
class GifRepositoryTest {
  private val service = mock<TenorService>()
  private val next = "0.0"
  private val response = TenorResponseDto()

  private lateinit var sut: GifRepository

  @Before
  fun setUp() {
    sut = GifRepository(service)
  }

  @Test
  fun testLoadTrendingImagesSuccess() = runBlocking {
    whenever(service.fetchTrendingResults(eq(next)))
      .thenReturn(Response.success(response))

    val result = sut.getTrendingResults(next).data

    verify(service).fetchTrendingResults(eq(next))
    assertThat(result).isEqualTo(response)
  }

  @Test
  fun testLoadTrendingImagesError() = runBlocking {
    val errorResponse = okhttp3.Response.Builder()
      .code(HTTP_INTERNAL_ERROR)
      .message("Broken!")
      .protocol(HTTP_1_1)
      .request(okhttp3.Request.Builder().url(TestApiConfigModule().provideBaseUrl()).build())
      .build()
    val plainText = "text/plain; charset=utf-8".toMediaType()
    val errorBody = "Broken!".toResponseBody(plainText)
    whenever(service.fetchTrendingResults(eq(next)))
      .thenReturn(Response.error(errorBody, errorResponse))

    val result = sut.getTrendingResults(next).data

    verify(service).fetchTrendingResults(eq(next))
    assertThat(result).isNull()
  }

  @Test
  fun testLoadSearchImagesSuccess() = runBlocking {
    val searchString = "gifs"
    whenever(service.fetchSearchResults(eq(searchString), eq(next)))
      .thenReturn(Response.success(response))

    val result = sut.getSearchResults(searchString, next).data

    verify(service).fetchSearchResults(eq(searchString), eq(next))
    assertThat(result).isEqualTo(response)
  }

  @Test
  fun testLoadSearchImagesError() = runBlocking {
    val searchString = "gifs"
    val errorResponse = okhttp3.Response.Builder()
      .code(HTTP_INTERNAL_ERROR)
      .message("Broken!")
      .protocol(HTTP_1_1)
      .request(okhttp3.Request.Builder().url(TestApiConfigModule().provideBaseUrl()).build())
      .build()
    val plainText = "text/plain; charset=utf-8".toMediaType()
    val errorBody = "Broken!".toResponseBody(plainText)
    whenever(service.fetchSearchResults(eq(searchString), eq(next)))
      .thenReturn(Response.error(errorBody, errorResponse))

    val result = sut.getSearchResults(searchString, next).data

    verify(service).fetchSearchResults(eq(searchString), eq(next))
    assertThat(result).isNull()
  }
}
