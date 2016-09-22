package burrows.apps.gif.example.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class Result {
  @SerializedName("media") @Expose private List<Media> media = new ArrayList<>();
  @SerializedName("title") @Expose private String title;

  /**
   * No args constructor for use in serialization
   */
  public Result() {
  }

  public Result(final List<Media> media, final String title) {
    this.media = media;
    this.title = title;
  }

  /**
   * @return The media
   */
  public List<Media> getMedia() {
    return media;
  }

  /**
   * @param media The media
   */
  public void setMedia(final List<Media> media) {
    this.media = media;
  }

  public Result withMedia(final List<Media> media) {
    this.media = media;
    return this;
  }

  /**
   * @return The title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title The title
   */
  public void setTitle(final String title) {
    this.title = title;
  }

  public Result withTitle(final String title) {
    this.title = title;
    return this;
  }
}
