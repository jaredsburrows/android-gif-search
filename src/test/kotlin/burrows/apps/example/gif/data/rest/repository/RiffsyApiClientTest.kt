package burrows.apps.example.gif.data.rest.repository

import burrows.apps.example.gif.data.rest.model.RiffsyResponseDto
import burrows.apps.example.gif.data.rest.repository.RiffsyApiClient.Companion.DEFAULT_LIMIT_COUNT
import io.reactivex.observers.TestObserver
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import com.google.common.truth.Truth.assertThat
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import test.TestBase
import java.net.HttpURLConnection.HTTP_NOT_FOUND

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class RiffsyApiClientTest : TestBase() {
  companion object {
    private val server = MockWebServer()

    @BeforeClass @JvmStatic fun setUpClass() {
      server.start(MOCK_SERVER_PORT)
      server.setDispatcher(dispatcher)
    }

    @AfterClass @JvmStatic fun tearDownClass() {
      server.shutdown()
    }

    private val dispatcher = object : Dispatcher() {
      override fun dispatch(request: RecordedRequest): MockResponse = when {
        request.path.contains("/v1/trending") -> getMockResponse("/trending_results.json")
        request.path.contains("/v1/search") -> getMockResponse("/search_results.json")
        else -> MockResponse().setResponseCode(HTTP_NOT_FOUND)
      }
    }
  }

  private lateinit var sut: RiffsyApiClient

  @Before override fun setUp() {
    super.setUp()

    sut = getRetrofit(server.url("/").toString()).build().create(RiffsyApiClient::class.java)
  }

  @Test fun testTrendingResultsUrlShouldParseCorrectly() {
    // Arrange
    val observer = TestObserver<RiffsyResponseDto>()

    // Act
    val observable = sut.getTrendingResults(DEFAULT_LIMIT_COUNT, null)
    val response = observable.blockingFirst()
    observer.assertNoErrors()

    // Assert
    assertThat(response.results?.get(0)?.media?.get(0)?.gif?.url)
      .contains("/images/7d95a1f8a8750460a82b04451be26d69/raw")
  }

  @Test fun testTrendingResultsUrlPreviewShouldParseCorrectly() {
    // Arrange
    val observer = TestObserver<RiffsyResponseDto>()

    // Act
    val observable = sut.getTrendingResults(DEFAULT_LIMIT_COUNT, null)
    val response = observable.blockingFirst()
    observer.assertNoErrors()

    // Assert
    assertThat(response.results?.get(0)?.media?.get(0)?.gif?.preview)
      .contains("/images/511fdce5dc8f5f2b88ac2de6c74b92e7/raw")
  }

  @Test fun testSearchResultsUrlShouldParseCorrectly() {
    // Arrange
    val observer = TestObserver<RiffsyResponseDto>()

    // Act
    val observable = sut.getSearchResults("hello", DEFAULT_LIMIT_COUNT, null)
    val response = observable.blockingFirst()
    observer.assertNoErrors()

    // Assert
    assertThat(response.results?.get(0)?.media?.get(0)?.gif?.url)
      .contains("/images/6088f94e6eb5dd7584dedda0fe1e52e1/raw")
  }

  @Test fun testSearchResultsUrlPreviewShouldParseCorrectly() {
    // Arrange
    val observer = TestObserver<RiffsyResponseDto>()

    // Act
    val observable = sut.getSearchResults("hello", DEFAULT_LIMIT_COUNT, null)
    val response = observable.blockingFirst()
    observer.assertNoErrors()

    // Assert
    assertThat(response.results?.get(0)?.media?.get(0)?.gif?.preview)
      .contains("/images/6f2ed339fbdb5c1270e29945ee1f0d77/raw")
  }

  private fun getRetrofit(baseUrl: String): Retrofit.Builder {
    return Retrofit.Builder()
      .baseUrl(baseUrl)
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(MoshiConverterFactory.create())
      .client(OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor()
          .setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()
      )
  }
}
