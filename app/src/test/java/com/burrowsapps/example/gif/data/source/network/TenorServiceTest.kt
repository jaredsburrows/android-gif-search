package com.burrowsapps.example.gif.data.source.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import test.TestFileUtils.MOCK_SERVER_PORT
import test.TestFileUtils.getMockFileResponse
import test.TestFileUtils.getMockResponse
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
class TenorServiceTest {
  @get:Rule(order = 0)
  val hiltRule = HiltAndroidRule(this)

  @Inject internal lateinit var sut: TenorService

  private val server = MockWebServer()

  @Before
  fun setUp() {
    hiltRule.inject()

    server.apply {
      dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
          request.path.orEmpty().apply {
            return when {
              contains("v1/trending") -> getMockResponse("/trending_results.json")
              contains("v1/search") -> getMockResponse("/search_results.json")
              contains("images") -> getMockFileResponse("/ic_launcher.webp")
              else -> MockResponse().setResponseCode(HTTP_NOT_FOUND)
            }
          }
        }
      }

      start(MOCK_SERVER_PORT)
    }
  }

  @After
  fun tearDown() {
    server.shutdown()
  }

  @Test
  fun testTrendingResultsURLShouldParseCorrectly() = runTest {
    val response = sut.getTrendingResults(null)
    val body = response.body()!!

    assertThat(body.results[0].media[0].tinyGif.url)
      .contains("/images/7d95a1f8a8750460a82b04451be26d69/raw")
  }

  @Test
  fun testTrendingResultsURLPreviewShouldParseCorrectly() = runTest {
    val response = sut.getTrendingResults(null)
    val body = response.body()!!

    assertThat(body.results[0].media[0].tinyGif.preview)
      .contains("/images/511fdce5dc8f5f2b88ac2de6c74b92e7/raw")
  }

  @Test
  fun testSearchResultsURLShouldParseCorrectly() = runTest {
    val response = sut.getSearchResults("hello", null)
    val body = response.body()!!

    assertThat(body.results[0].media[0].tinyGif.url)
      .contains("/images/6088f94e6eb5dd7584dedda0fe1e52e1/raw")
  }

  @Test
  fun testSearchResultsURLPreviewShouldParseCorrectly() = runTest {
    val response = sut.getSearchResults("hello", null)
    val body = response.body()!!

    assertThat(body.results[0].media[0].tinyGif.preview)
      .contains("/images/6f2ed339fbdb5c1270e29945ee1f0d77/raw")
  }
}
