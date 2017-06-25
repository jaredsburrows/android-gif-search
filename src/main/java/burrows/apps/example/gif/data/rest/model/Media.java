package burrows.apps.example.gif.data.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class Media {
  @SerializedName("tinygif") private Gif gif;

  /**
   * No args constructor for use in serialization
   */
  public Media() {
    this(new Builder());
  }

  public Media(Builder builder) {
    this.gif = builder.gif;
  }

  public Gif gif() {
    return gif;
  }

  public Builder newBuilder() {
    return new Builder(this);
  }

  public static class Builder {
    private Gif gif;

    public Builder() {
    }

    public Builder(Media media) {
      this.gif = media.gif;
    }

    public Builder gif(Gif gif) {
      this.gif = gif;
      return this;
    }

    public Media build() {
      return new Media(this);
    }
  }
}
