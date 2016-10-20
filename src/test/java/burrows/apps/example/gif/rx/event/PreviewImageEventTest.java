package burrows.apps.example.gif.rx.event;

import org.junit.Test;
import test.TestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class PreviewImageEventTest extends TestBase {
  @Test public void testGetImageInfo() {
    assertThat(new PreviewImageEvent(STRING_UNIQUE).getUrl()).isEqualTo(STRING_UNIQUE);
  }
}
