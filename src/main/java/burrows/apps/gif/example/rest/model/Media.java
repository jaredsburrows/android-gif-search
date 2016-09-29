package burrows.apps.gif.example.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class Media {
  @SerializedName("gif") @Expose private Gif gif;

  /**
   * No args constructor for use in serialization
   */
  public Media() {
  }

  public Media(Gif gif) {
    this.gif = gif;
  }

  /**
   * @return The gif
   */
  public Gif getGif() {
    return gif;
  }

  /**
   * @param gif The gif
   */
  public void setGif(Gif gif) {
    this.gif = gif;
  }

  public Media withGif(Gif gif) {
    this.gif = gif;
    return this;
  }
}
