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
  @SerializedName("next") final Float next;

  /**
   * No args constructor for use in serialization
   */
  public RiffsyResponse() {
    this(new Builder());
  }

  public RiffsyResponse(Builder builder) {
    this.results = builder.results;
    this.next = builder.next;
  }

  public List<Result> results() {
    return results;
  }

  public Float next() {
    return next;
  }

  public Builder newBuilder() {
    return new Builder(this);
  }

  public static class Builder {
    List<Result> results;
    Float next;

    public Builder() {
    }

    public Builder(RiffsyResponse response) {
      this.results = response.results;
      this.next = response.next;
    }

    public Builder results(List<Result> results) {
      this.results = results;
      return this;
    }

    public Builder next(Float next) {
      this.next = next;
      return this;
    }

    public RiffsyResponse build() {
      return new RiffsyResponse(this);
    }
  }
}
