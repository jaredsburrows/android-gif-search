package burrows.apps.giphy.example.rx.event;

import burrows.apps.giphy.example.ui.adapter.model.GiphyImageInfo;

/**
 * Eventbus event for sending ImageInfo.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class PreviewImageEvent {
    private final GiphyImageInfo imageInfo;

    public PreviewImageEvent(final GiphyImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }

    /**
     * Get the ImageInfo from the event.
     *
     * @return ImageInfo from the event.
     */
    public GiphyImageInfo getImageInfo() {
        return this.imageInfo;
    }
}
