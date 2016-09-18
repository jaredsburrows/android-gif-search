package burrows.apps.giphy.example.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Giphy Api Response for the fixed data gif.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class FixedHeight {
  @SerializedName("url") @Expose private String url;

  /**
   * No args constructor for use in serialization
   */
  public FixedHeight() {
  }

  /**
   * @param url
   */
  public FixedHeight(final String url) {
    this.url = url;
  }

  /**
   * @return The url
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url The url
   */
  public void setUrl(String url) {
    this.url = url;
  }
}
