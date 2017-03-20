package burrows.apps.example.gif.presentation.adapter.model;

/**
 * Model for the GifAdapter in order to display the gifs.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class ImageInfoModel {
  private final String url;
  private final String previewUrl;

  public ImageInfoModel() {
    this(new Builder());
  }

  public ImageInfoModel(Builder builder) {
    this.url = builder.url;
    this.previewUrl = builder.previewUrl;
  }

  public String url() {
    return url;
  }

  public String previewUrl() {
    return previewUrl;
  }

  public Builder newBuilder() {
    return new Builder(this);
  }

  public static class Builder {
    private String url;
    private String previewUrl;

    public Builder() {
    }

    public Builder(ImageInfoModel imageInfo) {
      this.url = imageInfo.url;
      this.previewUrl = imageInfo.previewUrl;
    }

    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public Builder previewUrl(String previewUrl) {
      this.previewUrl = previewUrl;
      return this;
    }

    public ImageInfoModel build() {
      return new ImageInfoModel(this);
    }
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    final ImageInfoModel imageInfo = (ImageInfoModel) o;

    if (url != null ? !url.equals(imageInfo.url) : imageInfo.url != null) return false;

    return previewUrl != null ? previewUrl.equals(imageInfo.previewUrl)
      : imageInfo.previewUrl == null;
  }

  @Override public int hashCode() {
    int result = url != null ? url.hashCode() : 0;
    result = 31 * result + (previewUrl != null ? previewUrl.hashCode() : 0);
    return result;
  }
}
