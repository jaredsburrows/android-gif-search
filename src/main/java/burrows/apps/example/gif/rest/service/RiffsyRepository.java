package burrows.apps.example.gif.rest.service;

import burrows.apps.example.gif.rest.model.RiffsyResponse;
import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Riffsy Api Service for getting "trending" and "search" api results.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class RiffsyRepository {
  /**
   * Contains the methods that will be used for retrieving the model.
   */
  private static RiffsyApi api;

  /**
   * Riffsy service.
   *
   * @param retrofit Retrofit.Builder.
   */
  public RiffsyRepository(Retrofit.Builder retrofit) {
    this(retrofit, RiffsyApi.BASE_URL);
  }

  /**
   * Riffsy service.
   *
   * @param retrofit Retrofit.Builder.
   * @param endPoint Base URL.
   */
  public RiffsyRepository(Retrofit.Builder retrofit, String endPoint) {
    api = retrofit.baseUrl(endPoint).build().create(RiffsyApi.class);
  }

  /**
   * Get search gif results by a search string.
   *
   * @param limit Limit results.
   * @return Response of trending results.
   */
  public Observable<RiffsyResponse> getTrendingResults(int limit) {
    return api.getTrendingResults(limit);
  }

  /**
   * Get search gif results by a search string.
   *
   * @param tag   Search string to find gifs.
   * @param limit Limit results.
   * @return Response of search results.
   */
  public Observable<RiffsyResponse> getSearchResults(String tag, int limit) {
    return api.getSearchResults(tag, limit);
  }

  /**
   * Custom Api interfaces for the Riffsy Api.
   *
   * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
   */
  public interface RiffsyApi {
    /**
     * URL for Riffsy.
     */
    String BASE_URL = "https://api.riffsy.com/";

    /**
     * Riffsy public API key.
     */
    String API_KEY = "LIVDSRZULELA";

    /**
     * Riffsy limit results.
     */
    int DEFAULT_LIMIT_COUNT = 24;

    /**
     * Get trending gif results.
     * <p>
     * URL: https://api.riffsy.com/
     * Path: /v1/trending
     * Query: limit
     * Query: key
     *
     * @param limit Limit results.
     * @return Response of trending results.
     */
    @GET("/v1/trending?key=" + API_KEY) Observable<RiffsyResponse> getTrendingResults(@Query("limit") int limit);

    /**
     * Get search gif results by a search string.
     * <p>
     * URL: https://api.riffsy.com/
     * Path: /v1/search
     * Query: q
     * Query: limit
     * Query: key
     *
     * @param tag   Search string to find gifs.
     * @param limit Limit results.
     * @return Response of search results.
     */
    @GET("/v1/search?key=" + API_KEY) Observable<RiffsyResponse> getSearchResults(@Query("tag") String tag,
                                                                                  @Query("limit") int limit);
  }
}
