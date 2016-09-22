package burrows.apps.gif.example.rest.service;

import burrows.apps.gif.example.rest.model.RiffsyResponse;
import okhttp3.mockwebserver.MockResponse;
import org.junit.After;
import org.junit.Test;
import test.ServiceTestBase;

import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class RiffsyServiceTest extends ServiceTestBase {
  @After @Override public void tearDown() throws Exception {
    super.tearDown();

    // Destroy singletons
    final Field field = RiffsyService.class.getDeclaredField("instance");
    field.setAccessible(true);
    field.set(null, null);
  }

  // --------------------------------------------
  // getTrendingResults
  // --------------------------------------------

  @Test public void testGetTrendingDataFromApiShouldParseCorrectly() {
    // Response
    final String mockResponse = new Scanner(getClass().getResourceAsStream("/trending_results.json"), Charset.defaultCharset().name()).useDelimiter("\\A").next();

    server.enqueue(new MockResponse()
      .setResponseCode(HttpURLConnection.HTTP_OK)
      .setBody(mockResponse));

    // Request
    final RiffsyResponse response = RiffsyService.getInstance(mockEndPoint)
      .getTrendingResults()
      .blockingFirst();

    assertThat(response.getResults().get(0).getMedia().get(0).getGif().getUrl())
      .isEqualTo("https://media.riffsy.com/images/f54932e6b9553a5538f31a5ddd78a9f3/raw");
  }

  @Test public void testGetTrendingDataWithCustomApiKeyFromApiShouldParseCorrectly() {
    // Response
    final String mockResponse = new Scanner(getClass().getResourceAsStream("/trending_results.json"), Charset.defaultCharset().name()).useDelimiter("\\A").next();

    server.enqueue(new MockResponse()
      .setResponseCode(HttpURLConnection.HTTP_OK)
      .setBody(mockResponse));

    // Request
    final RiffsyResponse response = RiffsyService.getInstance(mockEndPoint)
      .getTrendingResults(RiffsyService.PUBLIC_API_KEY)
      .blockingFirst();

    assertThat(response.getResults().get(0).getMedia().get(0).getGif().getUrl())
      .isEqualTo("https://media.riffsy.com/images/f54932e6b9553a5538f31a5ddd78a9f3/raw");
  }

  @Test
  public void testGetTrendingDataWithLimitWithCustomApiKeyFromApiShouldParseCorrectly() {
    // Response
    final String mockResponse = new Scanner(getClass().getResourceAsStream("/trending_results.json"), Charset.defaultCharset().name()).useDelimiter("\\A").next();

    server.enqueue(new MockResponse()
      .setResponseCode(HttpURLConnection.HTTP_OK)
      .setBody(mockResponse));

    // Request
    final RiffsyResponse response = RiffsyService.getInstance(mockEndPoint)
      .getTrendingResults(RiffsyService.DEFAULT_RESULTS_COUNT, RiffsyService.PUBLIC_API_KEY)
      .blockingFirst();

    assertThat(response.getResults().get(0).getMedia().get(0).getGif().getUrl())
      .isEqualTo("https://media.riffsy.com/images/f54932e6b9553a5538f31a5ddd78a9f3/raw");
  }

  // --------------------------------------------
  // getSearchResults
  // --------------------------------------------

  @Test public void testGetSearchDataFromApiShouldParseCorrectly() {
    // Response
    final String mockResponse = new Scanner(getClass().getResourceAsStream("/search_results.json"), Charset.defaultCharset().name()).useDelimiter("\\A").next();

    server.enqueue(new MockResponse()
      .setResponseCode(HttpURLConnection.HTTP_OK)
      .setBody(mockResponse));

    // Request
    final RiffsyResponse response = RiffsyService.getInstance(mockEndPoint)
      .getSearchResults("funny cat")
      .blockingFirst();

    assertThat(response.getResults().get(0).getMedia().get(0).getGif().getUrl())
      .isEqualTo("https://media.riffsy.com/images/5b6a39aa00312575583031d2de4edbd4/raw");
  }

  @Test public void testGetSearchDataCustomApiKeyFromApiShouldParseCorrectly() {
    // Response
    final String mockResponse = new Scanner(getClass().getResourceAsStream("/search_results.json"), Charset.defaultCharset().name()).useDelimiter("\\A").next();

    server.enqueue(new MockResponse()
      .setResponseCode(HttpURLConnection.HTTP_OK)
      .setBody(mockResponse));

    // Request
    final RiffsyResponse response = RiffsyService.getInstance(mockEndPoint)
      .getSearchResults("funny cat", RiffsyService.PUBLIC_API_KEY)
      .blockingFirst();

    assertThat(response.getResults().get(0).getMedia().get(0).getGif().getUrl())
      .isEqualTo("https://media.riffsy.com/images/5b6a39aa00312575583031d2de4edbd4/raw");
  }

  @Test public void testGetSearchDataWithLimitCustomApiKeyFromApiShouldParseCorrectly() {
    // Response
    final String mockResponse = new Scanner(getClass().getResourceAsStream("/search_results.json"), Charset.defaultCharset().name()).useDelimiter("\\A").next();

    server.enqueue(new MockResponse()
      .setResponseCode(HttpURLConnection.HTTP_OK)
      .setBody(mockResponse));

    // Request
    final RiffsyResponse response = RiffsyService.getInstance(mockEndPoint)
      .getSearchResults("funny cat", RiffsyService.DEFAULT_RESULTS_COUNT, RiffsyService.PUBLIC_API_KEY)
      .blockingFirst();

    assertThat(response.getResults().get(0).getMedia().get(0).getGif().getUrl())
      .isEqualTo("https://media.riffsy.com/images/5b6a39aa00312575583031d2de4edbd4/raw");
  }
}
