package burrows.apps.gif.example.rest.service;

import burrows.apps.gif.example.rest.model.RiffsyResponse;
import okhttp3.mockwebserver.MockResponse;
import org.junit.Test;
import test.ServiceTestBase;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class RiffsyServiceTest extends ServiceTestBase {
  private void sendMockMessages(String fileName) throws Exception {
    final InputStream stream = getClass().getResourceAsStream(fileName);
    final String mockResponse = new Scanner(stream, Charset.defaultCharset().name())
      .useDelimiter("\\A").next();
    server.enqueue(new MockResponse()
      .setResponseCode(HttpURLConnection.HTTP_OK)
      .setBody(mockResponse));
    stream.close();
  }

  @Test public void testGetTrendingDataFromApiShouldParseCorrectly() throws Exception {
    // Response
    sendMockMessages("/trending_results.json");

    // Request
    final RiffsyResponse response = new RiffsyService(mockEndPoint)
      .getTrendingResults()
      .blockingFirst();

    assertThat(response.getResults().get(0).getMedia().get(0).getGif().getUrl())
      .isEqualTo("https://media.riffsy.com/images/f54932e6b9553a5538f31a5ddd78a9f3/raw");
  }

  @Test public void testGetTrendingDataWithCustomApiKeyFromApiShouldParseCorrectly() throws Exception {
    // Response
    sendMockMessages("/trending_results.json");

    // Request
    final RiffsyResponse response = new RiffsyService(mockEndPoint)
      .getTrendingResults(RiffsyService.PUBLIC_API_KEY)
      .blockingFirst();

    assertThat(response.getResults().get(0).getMedia().get(0).getGif().getUrl())
      .isEqualTo("https://media.riffsy.com/images/f54932e6b9553a5538f31a5ddd78a9f3/raw");
  }

  @Test
  public void testGetTrendingDataWithLimitWithCustomApiKeyFromApiShouldParseCorrectly() throws Exception {
    // Response
    sendMockMessages("/trending_results.json");

    // Request
    final RiffsyResponse response = new RiffsyService(mockEndPoint)
      .getTrendingResults(RiffsyService.DEFAULT_RESULTS_COUNT, RiffsyService.PUBLIC_API_KEY)
      .blockingFirst();

    assertThat(response.getResults().get(0).getMedia().get(0).getGif().getUrl())
      .isEqualTo("https://media.riffsy.com/images/f54932e6b9553a5538f31a5ddd78a9f3/raw");
  }

  @Test public void testGetSearchDataFromApiShouldParseCorrectly() throws Exception {
    // Response
    sendMockMessages("/search_results.json");

    // Request
    final RiffsyResponse response = new RiffsyService(mockEndPoint)
      .getSearchResults("funny cat")
      .blockingFirst();

    assertThat(response.getResults().get(0).getMedia().get(0).getGif().getUrl())
      .isEqualTo("https://media.riffsy.com/images/5b6a39aa00312575583031d2de4edbd4/raw");
  }

  @Test public void testGetSearchDataCustomApiKeyFromApiShouldParseCorrectly() throws Exception {
    // Response
    sendMockMessages("/search_results.json");

    // Request
    final RiffsyResponse response = new RiffsyService(mockEndPoint)
      .getSearchResults("funny cat", RiffsyService.PUBLIC_API_KEY)
      .blockingFirst();

    assertThat(response.getResults().get(0).getMedia().get(0).getGif().getUrl())
      .isEqualTo("https://media.riffsy.com/images/5b6a39aa00312575583031d2de4edbd4/raw");
  }

  @Test public void testGetSearchDataWithLimitCustomApiKeyFromApiShouldParseCorrectly() throws Exception {
    // Response
    sendMockMessages("/search_results.json");

    // Request
    final RiffsyResponse response = new RiffsyService(mockEndPoint)
      .getSearchResults("funny cat", RiffsyService.DEFAULT_RESULTS_COUNT, RiffsyService.PUBLIC_API_KEY)
      .blockingFirst();

    assertThat(response.getResults().get(0).getMedia().get(0).getGif().getUrl())
      .isEqualTo("https://media.riffsy.com/images/5b6a39aa00312575583031d2de4edbd4/raw");
  }
}
