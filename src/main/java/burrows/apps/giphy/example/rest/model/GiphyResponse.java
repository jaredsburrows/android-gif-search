package burrows.apps.giphy.example.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Giphy Api Response.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class GiphyResponse {
    @SerializedName("data") @Expose private List<Data> mData = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     */
    public GiphyResponse() {
    }

    /**
     * @param data
     */
    public GiphyResponse(final List<Data> data) {
        this.mData = data;
    }

    /**
     * @return The data
     */
    public List<Data> getData() {
        return this.mData;
    }

    /**
     * @param data The data
     */
    public void setData(final List<Data> data) {
        this.mData = data;
    }
}
