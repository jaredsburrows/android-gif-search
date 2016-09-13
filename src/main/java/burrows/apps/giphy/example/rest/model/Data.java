package burrows.apps.giphy.example.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Giphy Api Response list of images.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class Data {
    @SerializedName("images") @Expose private Images mImages;

    /**
     * No args constructor for use in serialization
     */
    public Data() {
    }

    public Data(final Images images) {
        this.mImages = images;
    }

    /**
     * @return The images
     */
    public Images getImages() {
        return this.mImages;
    }

    /**
     * @param images The images
     */
    public void setImages(final Images images) {
        this.mImages = images;
    }
}
