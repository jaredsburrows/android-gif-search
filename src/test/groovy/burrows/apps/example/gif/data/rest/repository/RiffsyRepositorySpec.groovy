package burrows.apps.example.gif.data.rest.repository

import burrows.apps.example.gif.data.rest.model.RiffsyResponse
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import test.BaseSpec

import java.nio.charset.Charset

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
final class RiffsyRepositorySpec extends BaseSpec {
  @Rule MockWebServer server = new MockWebServer()
  RiffsyRepository sut

  def sendMockMessages(String fileName) throws Throwable {
    InputStream stream = getClass().getResourceAsStream(fileName)
    String mockResponse = ++new Scanner(stream, Charset.defaultCharset().name())
            .useDelimiter("\\A")

    server.enqueue(new MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(mockResponse))

    stream.close()
  }

  static Retrofit.Builder getRetrofit(String endPoint) {
    return new Retrofit.Builder()
            .baseUrl(endPoint)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(new Gson()))
            .client(new OkHttpClient())
  }

  def "setup"() {
    String mockEndPoint = server.url("/").toString()

    sut = getRetrofit(mockEndPoint).build().create(RiffsyRepository.class)
  }

  def "cleanup"() {
    server.shutdown()
  }

  def "trending results url should parse correctly"() {
    when:
    sendMockMessages("/trending_results.json")

    then:
    RiffsyResponse response = sut
            .getTrendingResults(RiffsyRepository.DEFAULT_LIMIT_COUNT, null)
            .blockingFirst()

    expect:
    response.results().get(0).media().get(0).gif().url() ==
            "https://media.riffsy.com/images/7d95a1f8a8750460a82b04451be26d69/raw"
  }

  def "trending results url preview should parse correctly"() {
    when:
    sendMockMessages("/trending_results.json")

    then:
    RiffsyResponse response = sut
            .getTrendingResults(RiffsyRepository.DEFAULT_LIMIT_COUNT, null)
            .blockingFirst()

    expect:
    response.results().get(0).media().get(0).gif().preview() ==
            "https://media.riffsy.com/images/511fdce5dc8f5f2b88ac2de6c74b92e7/raw"
  }

  def "search results url should parse correctly"() {
    when:
    sendMockMessages("/search_results.json")

    then:
    RiffsyResponse response = sut
            .getSearchResults("hello", RiffsyRepository.DEFAULT_LIMIT_COUNT, null)
            .blockingFirst()

    expect:
    response.results().get(0).media().get(0).gif().url() ==
            "https://media.riffsy.com/images/6088f94e6eb5dd7584dedda0fe1e52e1/raw"
  }

  def "search results url preview should parse correctly"() {
    when:
    sendMockMessages("/search_results.json")

    then:
    RiffsyResponse response = sut
            .getSearchResults("hello", RiffsyRepository.DEFAULT_LIMIT_COUNT, null)
            .blockingFirst()

    expect:
    response.results().get(0).media().get(0).gif().preview() ==
            "https://media.riffsy.com/images/6f2ed339fbdb5c1270e29945ee1f0d77/raw"
  }
}
