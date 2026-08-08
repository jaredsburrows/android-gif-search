package com.burrowsapps.gif.search.data.api

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
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
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
class GifServiceTest {
  @get:Rule(order = 0)
  internal val hiltRule = HiltAndroidRule(this)

  @Inject
  internal lateinit var sut: GifService

  private val server = MockWebServer()

  @Before
  fun setUp() {
    hiltRule.inject()

    server.apply {
      dispatcher =
        object : Dispatcher() {
          override fun dispatch(request: RecordedRequest): MockResponse {
            request.path.orEmpty().apply {
              return when {
                // Matches URL pattern for trending on Klipy with parameters
                matches(Regex("^/api/v1/[^/]+/gifs/trending.*")) -> getMockResponse(fileName = "/trending_results.json")

                // Matches URL pattern for search on Klipy with parameters
                matches(Regex("^/api/v1/[^/]+/gifs/search.*")) -> getMockResponse(fileName = "/search_results.json")

                // Handling image files with specific response
                matches(Regex(".*/[^/]+\\.(png|jpg|gif)$")) -> getMockGifResponse(fileName = "/ic_launcher.webp")

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
  fun testTrendingResultsURLShouldParseCorrectly() =
    runTest {
      val response = withContext(IO) { sut.fetchTrendingResults(1) }
      val body = response.body()!!

      assertThat(
        body.data.results
          .first()
          .file.sm.gif.url,
      ).matches("http.*localhost.*gif")
    }

  @Test
  fun testTrendingResultsURLPreviewShouldParseCorrectly() =
    runTest {
      val response = withContext(IO) { sut.fetchTrendingResults(1) }
      val body = response.body()!!

      assertThat(
        body.data.results
          .first()
          .file.sm.jpg.url,
      ).matches("http.*localhost.*jpg")
    }

  @Test
  fun testSearchResultsURLShouldParseCorrectly() =
    runTest {
      val response = withContext(IO) { sut.fetchSearchResults("hello", 1) }
      val body = response.body()!!

      assertThat(
        body.data.results
          .first()
          .file.sm.gif.url,
      ).matches("http.*localhost.*gif")
    }

  @Test
  fun testSearchResultsURLPreviewShouldParseCorrectly() =
    runTest {
      val response = withContext(IO) { sut.fetchSearchResults("hello", 1) }
      val body = response.body()!!

      assertThat(
        body.data.results
          .first()
          .file.sm.jpg.url,
      ).matches("http.*localhost.*jpg")
    }
}
