package burrows.apps.gif.example.rest.service;

import burrows.apps.gif.example.rest.model.RiffsyResponse;
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
    sut = new RiffsyRepository(getRetrofit(mockEndPoint), mockEndPoint);
  }

  @Test public void testGetTrendingDataWithShouldParseCorrectly() throws Exception {
    // Response
    sendMockMessages("/trending_results.json");

    // Request
    final RiffsyResponse response = sut
      .getTrendingResults()
      .blockingFirst();

    assertThat(response.getResults().get(0).getMedia().get(0).getGif().getUrl())
      .isEqualTo("https://media.riffsy.com/images/f54932e6b9553a5538f31a5ddd78a9f3/raw");
  }

  @Test
  public void testGetTrendingDataWithLimitWithShouldParseCorrectly() throws Exception {
    // Response
    sendMockMessages("/trending_results.json");

    // Request
    final RiffsyResponse response = sut
      .getTrendingResults(RiffsyRepository.DEFAULT_RESULTS_COUNT)
      .blockingFirst();

    assertThat(response.getResults().get(0).getMedia().get(0).getGif().getUrl())
      .isEqualTo("https://media.riffsy.com/images/f54932e6b9553a5538f31a5ddd78a9f3/raw");
  }

  @Test public void testGetSearchDataFromApiShouldParseCorrectly() throws Exception {
    // Response
    sendMockMessages("/search_results.json");

    // Request
    final RiffsyResponse response = sut
      .getSearchResults("funny cat")
      .blockingFirst();

    assertThat(response.getResults().get(0).getMedia().get(0).getGif().getUrl())
      .isEqualTo("https://media.riffsy.com/images/5b6a39aa00312575583031d2de4edbd4/raw");
  }

  @Test public void testGetSearchDataWithLimitShouldParseCorrectly() throws Exception {
    // Response
    sendMockMessages("/search_results.json");

    // Request
    final RiffsyResponse response = sut
      .getSearchResults("funny cat", RiffsyRepository.DEFAULT_RESULTS_COUNT)
      .blockingFirst();

    assertThat(response.getResults().get(0).getMedia().get(0).getGif().getUrl())
      .isEqualTo("https://media.riffsy.com/images/5b6a39aa00312575583031d2de4edbd4/raw");
  }
}
