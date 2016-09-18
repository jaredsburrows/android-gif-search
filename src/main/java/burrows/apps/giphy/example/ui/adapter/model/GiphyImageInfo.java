package burrows.apps.giphy.example.ui.adapter.model;

/**
 * Model for the GiphyAdapter in order to display the Giphy Gifs.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class GiphyImageInfo {
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
   * @return Updated instance of ImageInfo.
   */
  public void setUrl(final String url) {
    this.url = url;
  }

  /**
   * Set image URL.
   *
   * @param url Image URL.
   * @return Updated instance of ImageInfo.
   */
  public GiphyImageInfo withUrl(String url) {
    this.url = url;
    return this;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final GiphyImageInfo that = (GiphyImageInfo) o;

    return url != null ? url.equals(that.url) : that.url == null;
  }

  @Override public int hashCode() {
    return url != null ? url.hashCode() : 0;
  }
}
