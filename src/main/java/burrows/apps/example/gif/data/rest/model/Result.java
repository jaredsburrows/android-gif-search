package burrows.apps.example.gif.data.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class Result {
  @SerializedName("media") private List<Media> media;
  @SerializedName("title") private String title;

  /**
   * No args constructor for use in serialization
   */
  public Result() {
    this(new Builder());
  }

  public Result(Builder builder) {
    this.media = builder.media;
    this.title = builder.title;
  }

  public List<Media> media() {
    return media;
  }

  public String title() {
    return title;
  }

  public Builder newBuilder() {
    return new Builder(this);
  }

  public static class Builder {
    private List<Media> media;
    private String title;

    public Builder() {
    }

    public Builder(Result result) {
      this.media = result.media;
      this.title = result.title;
    }

    public Builder media(List<Media> media) {
      this.media = media;
      return this;
    }

    public Builder title(String title) {
      this.title = title;
      return this;
    }

    public Result build() {
      return new Result(this);
    }
  }
}
