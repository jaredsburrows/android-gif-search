package burrows.apps.example.gif.data.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Riffsy Api Response.
 * eg. https://api.riffsy.com/v1/search?key=LIVDSRZULELA&tag=goodluck&limit=10
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class RiffsyResponse {
  @SerializedName("results") final List<Result> results;

  /**
   * No args constructor for use in serialization
   */
  public RiffsyResponse() {
    this(new Builder());
  }

  public RiffsyResponse(Builder builder) {
    this.results = builder.results;
  }

  public List<Result> results() {
    return results;
  }

  public Builder newBuilder() {
    return new Builder(this);
  }

  public static class Builder {
    List<Result> results;

    public Builder() {
    }

    public Builder(RiffsyResponse response) {
      this.results = response.results;
    }

    public Builder results(List<Result> results) {
      this.results = results;
      return this;
    }

    public RiffsyResponse build() {
      return new RiffsyResponse(this);
    }
  }
}
