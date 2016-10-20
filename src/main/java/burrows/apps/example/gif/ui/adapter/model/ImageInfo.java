package burrows.apps.example.gif.ui.adapter.model;

/**
 * Model for the GifAdapter in order to display the gifs.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class ImageInfo {
  /**
   * Image URL.
   */
  private String url;

  /**
   * Get image URL.
   *
   * @return Image URL.
   */
  public String getUrl() {
    return url;
  }

  /**
   * Set image URL.
   *
   * @param url Image URL.
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Set image URL.
   *
   * @param url Image URL.
   * @return Updated instance of ImageInfo.
   */
  public ImageInfo withUrl(String url) {
    this.url = url;
    return this;
  }

  @Override public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    final ImageInfo that = (ImageInfo) object;

    return url != null ? url.equals(that.url) : that.url == null;
  }

  @Override public int hashCode() {
    return url != null ? url.hashCode() : 0;
  }
}
