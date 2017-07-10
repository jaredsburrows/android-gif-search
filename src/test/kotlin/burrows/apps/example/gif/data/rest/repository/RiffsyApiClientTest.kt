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
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import test.TestBase
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_OK

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class RiffsyApiClientTest : TestBase() {
  @get:Rule val server = MockWebServer()
  private lateinit var sut: RiffsyApiClient

  @Before override fun setUp() {
    super.setUp()

    sut = getRetrofit(server.url("/").toString()).build().create(RiffsyApiClient::class.java)

    server.setDispatcher(dispatcher)
  }

  @After override fun tearDown() {
    super.tearDown()

    server.shutdown()
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
      .isEqualTo("https://media.riffsy.com/images/7d95a1f8a8750460a82b04451be26d69/raw")
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
      .isEqualTo("https://media.riffsy.com/images/511fdce5dc8f5f2b88ac2de6c74b92e7/raw")
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
      .isEqualTo("https://media.riffsy.com/images/6088f94e6eb5dd7584dedda0fe1e52e1/raw")
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
      .isEqualTo("https://media.riffsy.com/images/6f2ed339fbdb5c1270e29945ee1f0d77/raw")
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

  private fun getMockResponse(fileName: String): MockResponse {
    return MockResponse()
      .setStatus("HTTP/1.1 200")
      .setResponseCode(HTTP_OK)
      .setBody(parseText(fileName))
      .addHeader("Content-type: application/json; charset=utf-8")
  }

  private fun parseText(fileName: String): String {
    val inputStream = javaClass.getResourceAsStream(fileName)
    val text = InputStreamReader(inputStream).readText()
    inputStream.close()
    return text
  }

  private val dispatcher = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
      when {
        request.path.contains("/v1/trending") -> return getMockResponse("/trending_results.json")
        request.path.contains("/v1/search") -> return getMockResponse("/search_results.json")
        else -> return MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
      }
    }
  }
}
