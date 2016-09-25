package burrows.apps.gif.example.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Riffsy Api Response.
 * eg. https://api.riffsy.com/v1/search?key=LIVDSRZULELA&tag=goodluck&limit=10
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class RiffsyResponse {
  @SerializedName("results") @Expose private List<Result> results = new ArrayList<>();

  /**
   * No args constructor for use in serialization
   */
  public RiffsyResponse() {
  }

  /**
   * @param results The results
   */
  public RiffsyResponse(final List<Result> results) {
    this.results = results;
  }

  /**
   * @return The results
   */
  public List<Result> getResults() {
    return results;
  }

  /**
   * @param results The results
   */
  public void setResults(final List<Result> results) {
    this.results = results;
  }

  public RiffsyResponse withGif(final List<Result> results) {
    this.results = results;
    return this;
  }
}
