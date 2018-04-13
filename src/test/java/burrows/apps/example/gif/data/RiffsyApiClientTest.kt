package burrows.apps.example.gif.data

import burrows.apps.example.gif.data.model.RiffsyResponseDto
import burrows.apps.example.gif.di.module.NetModule
import burrows.apps.example.gif.di.module.RiffsyModule
import com.google.common.truth.Truth.assertThat
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import test.TestUtils.MOCK_SERVER_PORT
import test.TestUtils.getMockResponse
import java.net.HttpURLConnection.HTTP_NOT_FOUND

class RiffsyApiClientTest {
  private val server = MockWebServer()
  private val dispatcher = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse = when {
      request.path.contains("/v1/trending") -> getMockResponse("/trending_results.json")
      request.path.contains("/v1/search") -> getMockResponse("/search_results.json")
      else -> MockResponse().setResponseCode(HTTP_NOT_FOUND)
    }
  }
  private lateinit var sut: RiffsyApiClient

  @Before fun setUp() {
    server.start(MOCK_SERVER_PORT)
    server.setDispatcher(dispatcher)

    sut = RiffsyModule(server.url("/").toString())
      .providesRiffsyApi(NetModule()
        .providesRetrofit(NetModule()
          .providesMoshi(), NetModule()
          .providesOkHttpClient(null)))
  }

  @After fun tearDown() {
    server.shutdown()
  }

  @Test fun testTrendingResultsUrlShouldParseCorrectly() {
    // Arrange
    val observer = TestObserver<RiffsyResponseDto>()

    // Act
    val observable = sut.getTrendingResults(RiffsyApiClient.DEFAULT_LIMIT_COUNT, null)
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
    val observable = sut.getTrendingResults(RiffsyApiClient.DEFAULT_LIMIT_COUNT, null)
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
    val observable = sut.getSearchResults("hello", RiffsyApiClient.DEFAULT_LIMIT_COUNT, null)
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
    val observable = sut.getSearchResults("hello", RiffsyApiClient.DEFAULT_LIMIT_COUNT, null)
    val response = observable.blockingFirst()
    observer.assertNoErrors()

    // Assert
    assertThat(response.results?.get(0)?.media?.get(0)?.gif?.preview)
      .contains("/images/6f2ed339fbdb5c1270e29945ee1f0d77/raw")
  }
}
