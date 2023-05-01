package com.burrowsapps.gif.search.data.source.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.di.ApiConfigModule
import com.burrowsapps.gif.search.test.TestFileUtils.MOCK_SERVER_PORT
import com.burrowsapps.gif.search.test.TestFileUtils.getMockGifResponse
import com.burrowsapps.gif.search.test.TestFileUtils.getMockResponse
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
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
@UninstallModules(ApiConfigModule::class)
@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
class TenorServiceTest {
  @get:Rule(order = 0)
  internal val hiltRule = HiltAndroidRule(this)

  @Inject
  internal lateinit var sut: TenorService

  private val server = MockWebServer()

  @Before
  fun setUp() {
    hiltRule.inject()

    server.apply {
      dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
          request.path.orEmpty().apply {
            return when {
              contains(other = "v1/trending") -> getMockResponse(fileName = "/trending_results.json")
              contains(other = "v1/search") -> getMockResponse(fileName = "/search_results.json")
              endsWith(suffix = ".png") || endsWith(suffix = ".gif") -> getMockGifResponse(fileName = "/ic_launcher.webp")
              else -> MockResponse().setResponseCode(code = HTTP_NOT_FOUND)
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
  fun testTrendingResultsURLShouldParseCorrectly() = runBlocking {
    val response = sut.getTrendingResults(null)
    val body = response.body()!!

    assertThat(body.results.first().media.first().tinyGif.url).matches("http.*localhost.*gif")
  }

  @Test
  fun testTrendingResultsURLPreviewShouldParseCorrectly() = runBlocking {
    val response = sut.getTrendingResults(null)
    val body = response.body()!!

    assertThat(body.results.first().media.first().tinyGif.preview).matches("http.*localhost.*png")
  }

  @Test
  fun testSearchResultsURLShouldParseCorrectly() = runBlocking {
    val response = sut.getSearchResults("hello", null)
    val body = response.body()!!

    assertThat(body.results.first().media.first().tinyGif.url).matches("http.*localhost.*gif")
  }

  @Test
  fun testSearchResultsURLPreviewShouldParseCorrectly() = runBlocking {
    val response = sut.getSearchResults("hello", null)
    val body = response.body()!!

    assertThat(body.results.first().media.first().tinyGif.preview).matches("http.*localhost.*png")
  }
}
