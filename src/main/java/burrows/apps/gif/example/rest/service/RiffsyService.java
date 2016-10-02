package burrows.apps.gif.example.rest.service;

import burrows.apps.gif.example.rest.model.RiffsyResponse;
import burrows.apps.gif.example.util.ServiceUtil;
import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Riffsy Api Service for getting "trending" and "search" api results.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class RiffsyService {
  /**
   * URL for Riffsy.
   */
  public static final String BASE_URL = "https://api.riffsy.com/";

  /**
   * Riffsy limit results.
   */
  public static final int DEFAULT_RESULTS_COUNT = 24;

  /**
   * Riffsy public API key.
   */
  public static final String PUBLIC_API_KEY = "LIVDSRZULELA";

  /**
   * Contains the methods that will be used for retrieving the model.
   */
  private static RiffsyServiceApi api;

  /**
   * Riffsy service.
   *
   * @param endPoint Base URL.
   */
  public RiffsyService() {
    this(BASE_URL);
  }

  /**
   * Riffsy service.
   *
   * @param endPoint Base URL.
   */
  public RiffsyService(String endPoint) {
    api = ServiceUtil.createService(RiffsyServiceApi.class, endPoint);
  }

  /**
   * Get trending gif results. Default limit to 24.
   *
   * @return Response of trending results.
   */
  public Observable<RiffsyResponse> getTrendingResults() {
    return getTrendingResults(PUBLIC_API_KEY);
  }

  /**
   * Get search gif results by a search string. Default limit to 24.
   *
   * @param key Api key for Riffsy.
   * @return Response of trending results.
   */
  public Observable<RiffsyResponse> getTrendingResults(String key) {
    return getTrendingResults(DEFAULT_RESULTS_COUNT, key);
  }

  /**
   * Get search gif results by a search string.
   *
   * @param limit Limit results.
   * @param key   Api key for Riffsy.
   * @return Response of trending results.
   */
  public Observable<RiffsyResponse> getTrendingResults(int limit, String key) {
    return api.getTrendingResults(limit, key);
  }

  /**
   * Get search gif results by a search string. Default limit to 24.
   *
   * @param tag Search string to find gifs.
   * @return Response of search results.
   */
  public Observable<RiffsyResponse> getSearchResults(String tag) {
    return getSearchResults(tag, PUBLIC_API_KEY);
  }

  /**
   * Get search gif results by a search string. Default limit to 24.
   *
   * @param tag Search string to find gifs.
   * @param key Api key for Riffsy.
   * @return Response of search results.
   */
  public Observable<RiffsyResponse> getSearchResults(String tag, String key) {
    return getSearchResults(tag, DEFAULT_RESULTS_COUNT, key);
  }

  /**
   * Get search gif results by a search string.
   *
   * @param tag   Search string to find gifs.
   * @param limit Limit results.
   * @param key   Api key for Riffsy.
   * @return Response of search results.
   */
  public Observable<RiffsyResponse> getSearchResults(String tag, int limit, String key) {
    return api.getSearchResults(tag, limit, key);
  }

  /**
   * Custom Api interfaces for the Riffsy Api.
   *
   * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
   */
  public interface RiffsyServiceApi {
    /**
     * Get trending gif results.
     * <p>
     * URL: https://api.riffsy.com/
     * Path: /v1/trending
     * Query: limit
     * Query: key
     *
     * @param limit Limit results.
     * @param key   Api key for Riffsy.
     * @return Response of trending results.
     */
    @GET("/v1/trending") Observable<RiffsyResponse> getTrendingResults(@Query("limit") int limit,
                                                                       @Query("key") String key);

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
     * @param key   Api key for Riffsy.
     * @return Response of search results.
     */
    @GET("/v1/search") Observable<RiffsyResponse> getSearchResults(@Query("tag") String tag,
                                                                   @Query("limit") int limit,
                                                                   @Query("key") String key);
  }
}
