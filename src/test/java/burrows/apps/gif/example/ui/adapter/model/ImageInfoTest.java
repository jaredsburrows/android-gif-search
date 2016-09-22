package burrows.apps.gif.example.ui.adapter.model;

import org.junit.Test;
import test.TestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class ImageInfoTest extends TestBase {
  private final ImageInfo sut = new ImageInfo().withUrl(STRING_UNIQUE);

  @Test public void getUrl() {
    assertThat(sut.getUrl()).isEqualTo(STRING_UNIQUE);
  }

  @Test public void withUrl() {
    sut.withUrl(STRING_UNIQUE2);

    assertThat(sut.getUrl()).isEqualTo(STRING_UNIQUE2);
  }
}
