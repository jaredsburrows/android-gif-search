package burrows.apps.giphy.example.event;

import burrows.apps.giphy.example.ui.adapter.model.GiphyImageInfo;
import org.junit.Test;
import test.TestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class PreviewImageEventTest extends TestBase {
  @Test
  public void testGetImageInfo() {
    assertThat(new PreviewImageEvent(new GiphyImageInfo().withUrl(STRING_UNIQUE)).getImageInfo().getUrl())
            .isEqualTo(STRING_UNIQUE);
  }
}
