package com.burrowsapps.example.gif.data.source.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.example.gif.test.TestFileUtils.MOCK_SERVER_PORT
import com.burrowsapps.example.gif.test.TestFileUtils.getMockFileResponse
import com.burrowsapps.example.gif.test.TestFileUtils.getMockResponse
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
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
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import javax.inject.Inject

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
      .contains("/images/7c05ba9bdad525e9b2ff5b45e276c7c6/tenor.gif")
  }

  @Test
  fun testTrendingResultsURLPreviewShouldParseCorrectly() = runTest {
    val response = sut.getTrendingResults(null)
    val body = response.body()!!

    assertThat(body.results[0].media[0].tinyGif.preview)
      .contains("/images/7b770b669d4b11c453256abefcd499f4/tenor.gif")
  }

  @Test
  fun testSearchResultsURLShouldParseCorrectly() = runTest {
    val response = sut.getSearchResults("hello", null)
    val body = response.body()!!

    assertThat(body.results[0].media[0].tinyGif.url)
      .contains("/images/f1b1086cd3f8ca158b09fd3ef522bdea/tenor.gif")
  }

  @Test
  fun testSearchResultsURLPreviewShouldParseCorrectly() = runTest {
    val response = sut.getSearchResults("hello", null)
    val body = response.body()!!

    assertThat(body.results[0].media[0].tinyGif.preview)
      .contains("/images/5fe0406aeeb8dbe28977f2336334b206/tenor.gif")
  }
}
