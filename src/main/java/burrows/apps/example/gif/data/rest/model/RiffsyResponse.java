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
  @SerializedName("results") private final List<Result> results;
  @SerializedName("page") private final Float page;

  /**
   * No args constructor for use in serialization
   */
  public RiffsyResponse() {
    this(new Builder());
  }

  public RiffsyResponse(Builder builder) {
    this.results = builder.results;
    this.page = builder.page;
  }

  public List<Result> results() {
    return results;
  }

  public Float page() {
    return page;
  }

  public Builder newBuilder() {
    return new Builder(this);
  }

  public static class Builder {
    private List<Result> results;
    private Float page;

    public Builder() {
    }

    public Builder(RiffsyResponse response) {
      this.results = response.results;
      this.page = response.page;
    }

    public Builder results(List<Result> results) {
      this.results = results;
      return this;
    }

    public Builder page(Float next) {
      this.page = next;
      return this;
    }

    public RiffsyResponse build() {
      return new RiffsyResponse(this);
    }
  }
}
