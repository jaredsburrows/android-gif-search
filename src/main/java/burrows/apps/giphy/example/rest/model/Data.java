package burrows.apps.giphy.example.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Giphy Api Response list of images.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class Data {

    @SerializedName("images")
    @Expose
    private Images images;

    /**
     * No args constructor for use in serialization
     */
    public Data() {
    }

    public Data(final Images images) {
        this.images = images;
    }

    /**
     * @return The images
     */
    public Images getImages() {
        return this.images;
    }

    /**
     * @param images The images
     */
    public void setImages(final Images images) {
        this.images = images;
    }
}
