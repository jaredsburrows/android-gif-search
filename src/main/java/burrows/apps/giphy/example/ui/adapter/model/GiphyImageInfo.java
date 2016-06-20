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
    private String mUrl;

    /**
     * Get image URL.
     *
     * @return Image URL.
     */
    public String getUrl() {
        return this.mUrl;
    }

    /**
     * Set image URL.
     *
     * @param url Image URL.
     * @return Updated instance of ImageInfo.
     */
    public GiphyImageInfo withUrl(final String url) {
        this.mUrl = url;

        return this;
    }
}
