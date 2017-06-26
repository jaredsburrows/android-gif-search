package burrows.apps.example.gif.data.rest.repository

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import test.TestBase
import java.net.HttpURLConnection
import java.nio.charset.Charset
import java.util.Scanner

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class RiffsyApiClientTest : TestBase() {
  @Rule @JvmField val server = MockWebServer()
  private var sut: RiffsyApiClient? = null

  @Before @Throws(Throwable::class)
  override fun setUp() {
    super.setUp()

    val mockEndPoint = server.url("/").toString()

    sut = getRetrofit(mockEndPoint).build().create(RiffsyApiClient::class.java)
  }

  @After @Throws(Throwable::class)
  override fun tearDown() {
    super.tearDown()

    server.shutdown()
  }

  @Throws(Throwable::class)
  private fun sendMockMessages(fileName: String) {
    val stream = javaClass.getResourceAsStream(fileName)
    val mockResponse = Scanner(stream, Charset.defaultCharset().name())
      .useDelimiter("\\A").next()

    server.enqueue(MockResponse()
      .setResponseCode(HttpURLConnection.HTTP_OK)
      .setBody(mockResponse))

    stream.close()
  }

  private fun getRetrofit(endPoint: String): Retrofit.Builder {
    return Retrofit.Builder()
      .baseUrl(endPoint)
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create(Gson()))
      .client(OkHttpClient())
  }

  @Test @Throws(Throwable::class)
  fun testTrendingResultsUrlShouldParseCorrectly() {
    // Arrange
    sendMockMessages("/trending_results.json")

    // Act
    val response = sut!!
      .getTrendingResults(RiffsyApiClient.DEFAULT_LIMIT_COUNT, null)
      .blockingFirst()

    // Assert
    assertThat(response?.results()?.get(0)?.media()?.get(0)?.gif()?.url())
      .isEqualTo("https://media.riffsy.com/images/7d95a1f8a8750460a82b04451be26d69/raw")
  }

  @Test @Throws(Throwable::class)
  fun testTrendingResultsUrlPreviewShouldParseCorrectly() {
    // Arrange
    sendMockMessages("/trending_results.json")

    // Act
    val response = sut!!
      .getTrendingResults(RiffsyApiClient.DEFAULT_LIMIT_COUNT, null)
      .blockingFirst()

    // Assert
    assertThat(response?.results()?.get(0)?.media()?.get(0)?.gif()?.preview())
      ?.isEqualTo("https://media.riffsy.com/images/511fdce5dc8f5f2b88ac2de6c74b92e7/raw")
  }

  @Test @Throws(Throwable::class)
  fun testSearchResultsUrlShouldParseCorrectly() {
    // Arrange
    sendMockMessages("/search_results.json")

    // Act
    val response = sut!!
      .getSearchResults("hello", RiffsyApiClient.DEFAULT_LIMIT_COUNT, null)
      .blockingFirst()

    // Assert
    assertThat(response?.results()?.get(0)?.media()?.get(0)?.gif()?.url())
      .isEqualTo("https://media.riffsy.com/images/6088f94e6eb5dd7584dedda0fe1e52e1/raw")
  }

  @Test @Throws(Throwable::class)
  fun testSearchResultsUrlPreviewShouldParseCorrectly() {
    // Arrange
    sendMockMessages("/search_results.json")

    // Act
    val response = sut!!
      .getSearchResults("hello", RiffsyApiClient.DEFAULT_LIMIT_COUNT, null)
      .blockingFirst()

    // Assert
    assertThat(response?.results()?.get(0)?.media()?.get(0)?.gif()?.preview())
      .isEqualTo("https://media.riffsy.com/images/6f2ed339fbdb5c1270e29945ee1f0d77/raw")
  }
}
