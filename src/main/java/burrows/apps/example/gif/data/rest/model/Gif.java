package burrows.apps.example.gif.data.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class Gif {
  @SerializedName("url") @Expose private String url;
  @SerializedName("preview") @Expose private String preview;

  /**
   * No args constructor for use in serialization
   */
  public Gif() {
  }

  public Gif(String url, String preview) {
    this.url = url;
    this.preview = preview;
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

  public Gif withUrl(String url) {
    this.url = url;
    return this;
  }

  /**
   * @return The preview
   */
  public String getPreview() {
    return preview;
  }

  /**
   * @param preview The preview
   */
  public void setPreview(String preview) {
    this.preview = preview;
  }

  public Gif withPreview(String preview) {
    this.preview = preview;
    return this;
  }
}
