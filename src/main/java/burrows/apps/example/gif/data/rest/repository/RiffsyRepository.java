package burrows.apps.example.gif.data.rest.repository;

import burrows.apps.example.gif.data.rest.model.RiffsyResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Riffsy Api Service for getting "trending" and "search" api results.
 * <p>
 * Custom Api interfaces for the Riffsy Api.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public interface RiffsyRepository {
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
   * Query: pos
   * eg. https://api.riffsy.com/v1/trending?key=LIVDSRZULELA&limit=10&pos=1
   *
   * @param limit Limit results.
   * @param pos   Position of results.
   * @return Response of trending results.
   */
  @GET("/v1/trending?key=" + API_KEY) Observable<RiffsyResponse> getTrendingResults(@Query("limit") int limit,
                                                                                    @Query("pos") Float pos); // Allow passing null

  /**
   * Get search gif results by a search string.
   * <p>
   * URL: https://api.riffsy.com/
   * Path: /v1/search
   * Query: q
   * Query: limit
   * Query: key
   * Query: pos
   * eg. https://api.riffsy.com/v1/search?key=LIVDSRZULELA&tag=goodluck&limit=10&pos=1
   *
   * @param tag   Search string to find gifs.
   * @param limit Limit results.
   * @param pos   Position of results.
   * @return Response of search results.
   */
  @GET("/v1/search?key=" + API_KEY) Observable<RiffsyResponse> getSearchResults(@Query("tag") String tag,
                                                                                @Query("limit") int limit,
                                                                                @Query("pos") Float pos); // Allow passing null
}
