package burrows.apps.giphy.example.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Giphy Api Response for image types.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class Images {

    @SerializedName("fixed_height")
    @Expose
    private FixedHeight fixedHeight;

    /**
     * No args constructor for use in serialization
     */
    public Images() {
    }

    /**
     * @param fixedHeight
     */
    public Images(final FixedHeight fixedHeight) {
        this.fixedHeight = fixedHeight;
    }

    /**
     * @return The fixedHeight
     */
    public FixedHeight getFixedHeight() {
        return this.fixedHeight;
    }

    /**
     * @param fixedHeight The fixed_height
     */
    public void setFixedHeight(final FixedHeight fixedHeight) {
        this.fixedHeight = fixedHeight;
    }
}
