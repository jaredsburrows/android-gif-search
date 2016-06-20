package burrows.apps.giphy.example.event;

import burrows.apps.giphy.example.ui.adapter.model.GiphyImageInfo;
import org.junit.Test;
import test.TestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class PreviewImageEventTest extends TestBase {

    private static final GiphyImageInfo IMAGE_INFO = new GiphyImageInfo().withUrl(STRING_UNIQUE);
    private PreviewImageEvent sut = new PreviewImageEvent(IMAGE_INFO);

    @Test
    public void testGetImageInfo() {
        assertThat(sut.getImageInfo().getUrl()).isEqualTo(STRING_UNIQUE);
    }
}
