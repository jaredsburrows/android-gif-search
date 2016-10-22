package burrows.apps.example.gif.presentation.adapter.model;

import org.junit.Test;
import test.TestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class ImageInfoTest extends TestBase {
  private ImageInfo sut = new ImageInfo.Builder().url(STRING_UNIQUE).build();

  @Test public void getUrl() {
    assertThat(sut.url()).isEqualTo(STRING_UNIQUE);
  }

  @Test public void withUrl() {
    sut = sut.newBuilder().url(STRING_UNIQUE2).build();

    assertThat(sut.url()).isEqualTo(STRING_UNIQUE2);
  }
}
