package com.burrowsapps.example.gif.data

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.example.gif.data.RiffsyApiService.Companion.DEFAULT_LIMIT_COUNT
import com.burrowsapps.example.gif.di.NetModule
import com.burrowsapps.example.gif.di.RiffsyModule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import test.TestFileUtils
import test.TestFileUtils.MOCK_SERVER_PORT
import test.TestFileUtils.getMockResponse
import java.net.HttpURLConnection.HTTP_NOT_FOUND

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class RiffsyApiClientTest {
  private val server = MockWebServer()
  private lateinit var sut: RiffsyApiService

  @Before fun setUp() {
    server.apply {
      dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
          request.path.orEmpty().apply {
            return when {
              contains("v1/trending") -> getMockResponse("/trending_results.json")
              contains("v1/search") -> getMockResponse("/search_results.json")
              contains("images") -> TestFileUtils.getMockFileResponse("/ic_launcher.png")
              else -> MockResponse().setResponseCode(HTTP_NOT_FOUND)
            }
          }
        }
      }

      start(MOCK_SERVER_PORT)
    }

    sut = RiffsyModule(server.url("/").toString()).provideRiffsyApi(
      NetModule.provideRetrofit(ApplicationProvider.getApplicationContext())
    )
  }

  @After fun tearDown() {
    server.shutdown()
  }

  @Test fun testTrendingResultsURLShouldParseCorrectly() = runTest {
    val response = sut.getTrendingResults(DEFAULT_LIMIT_COUNT, null)
    val body = response.body()!!

    assertThat(body.results[0].media[0].gif.url)
      .contains("/images/7d95a1f8a8750460a82b04451be26d69/raw")
  }

  @Test fun testTrendingResultsURLPreviewShouldParseCorrectly() = runTest {
    val response = sut.getTrendingResults(DEFAULT_LIMIT_COUNT, null)
    val body = response.body()!!

    assertThat(body.results[0].media[0].gif.preview)
      .contains("/images/511fdce5dc8f5f2b88ac2de6c74b92e7/raw")
  }

  @Test fun testSearchResultsURLShouldParseCorrectly() = runTest {
    val response = sut.getSearchResults("hello", DEFAULT_LIMIT_COUNT, null)
    val body = response.body()!!

    assertThat(body.results[0].media[0].gif.url)
      .contains("/images/6088f94e6eb5dd7584dedda0fe1e52e1/raw")
  }

  @Test fun testSearchResultsURLPreviewShouldParseCorrectly() = runTest {
    val response = sut.getSearchResults("hello", DEFAULT_LIMIT_COUNT, null)
    val body = response.body()!!

    assertThat(body.results[0].media[0].gif.preview)
      .contains("/images/6f2ed339fbdb5c1270e29945ee1f0d77/raw")
  }
}
