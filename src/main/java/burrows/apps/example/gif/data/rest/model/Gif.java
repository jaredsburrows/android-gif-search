package burrows.apps.example.gif.data.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class Gif {
  @SerializedName("url") private final String url;
  @SerializedName("preview") private final String preview;

  /**
   * No args constructor for use in serialization
   */
  public Gif() {
    this(new Builder());
  }

  public Gif(Builder builder) {
    this.url = builder.url;
    this.preview = builder.preview;
  }

  public String url() {
    return url;
  }

  public String preview() {
    return preview;
  }

  public Builder newBuilder() {
    return new Builder(this);
  }

  public static class Builder {
    private String url;
    private String preview;

    public Builder() {
    }

    public Builder(Gif gif) {
      this.url = gif.url;
      this.preview = gif.preview;
    }

    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public Builder preview(String preview) {
      this.preview = preview;
      return this;
    }

    public Gif build() {
      return new Gif(this);
    }
  }
}
