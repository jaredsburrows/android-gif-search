package burrows.apps.example.gif.data.rest.repository;

import burrows.apps.example.gif.data.rest.model.RiffsyResponse;
import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import test.ServiceTestBase;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class RiffsyRepositoryTest extends ServiceTestBase {
  private RiffsyRepository sut;

  private void sendMockMessages(String fileName) throws Exception {
    final InputStream stream = getClass().getResourceAsStream(fileName);
    final String mockResponse = new Scanner(stream, Charset.defaultCharset().name())
      .useDelimiter("\\A").next();

    server.enqueue(new MockResponse()
      .setResponseCode(HttpURLConnection.HTTP_OK)
      .setBody(mockResponse));

    stream.close();
  }

  private Retrofit.Builder getRetrofit(String endPoint) {
    return new Retrofit.Builder()
      .baseUrl(endPoint)
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create(new Gson()))
      .client(new OkHttpClient());
  }

  @Before @Override public void setUp() throws Exception {
    super.setUp();

    sut = getRetrofit(mockEndPoint).build().create(RiffsyRepository.class);
  }

  @Test public void testTrendingResultsUrlShouldParseCorrectly() throws Exception {
    // Response
    sendMockMessages("/trending_results.json");

    // Request
    final RiffsyResponse response = sut
      .getTrendingResults(RiffsyRepository.DEFAULT_LIMIT_COUNT)
      .blockingFirst();

    assertThat(response.results().get(0).media().get(0).gif().url())
      .isEqualTo("https://media.riffsy.com/images/7d95a1f8a8750460a82b04451be26d69/raw");
  }

  @Test public void testTrendingResultsUrlPreviewShouldParseCorrectly() throws Exception {
    // Response
    sendMockMessages("/trending_results.json");

    // Request
    final RiffsyResponse response = sut
      .getTrendingResults(RiffsyRepository.DEFAULT_LIMIT_COUNT)
      .blockingFirst();

    assertThat(response.results().get(0).media().get(0).gif().preview())
      .isEqualTo("https://media.riffsy.com/images/511fdce5dc8f5f2b88ac2de6c74b92e7/raw");
  }

  @Test public void testSearchResultsUrlShouldParseCorrectly() throws Exception {
    // Response
    sendMockMessages("/search_results.json");

    // Request
    final RiffsyResponse response = sut
      .getSearchResults("hello", RiffsyRepository.DEFAULT_LIMIT_COUNT)
      .blockingFirst();

    assertThat(response.results().get(0).media().get(0).gif().url())
      .isEqualTo("https://media.riffsy.com/images/6088f94e6eb5dd7584dedda0fe1e52e1/raw");
  }

  @Test public void testSearchResultsUrlPreviewShouldParseCorrectly() throws Exception {
    // Response
    sendMockMessages("/search_results.json");

    // Request
    final RiffsyResponse response = sut
      .getSearchResults("hello", RiffsyRepository.DEFAULT_LIMIT_COUNT)
      .blockingFirst();

    assertThat(response.results().get(0).media().get(0).gif().preview())
      .isEqualTo("https://media.riffsy.com/images/6f2ed339fbdb5c1270e29945ee1f0d77/raw");
  }
}
