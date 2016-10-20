package burrows.apps.example.gif.rest.model;

import org.junit.Test;
import test.TestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class GifTest extends TestBase {
  private final Gif sut = new Gif(STRING_UNIQUE, STRING_UNIQUE2).withUrl(STRING_UNIQUE).withPreview(STRING_UNIQUE2);

  @Test public void testGetUrl() {
    assertThat(sut.getUrl()).isEqualTo(STRING_UNIQUE);
  }

  @Test public void testSetUrl() {
    sut.setUrl(STRING_UNIQUE2);

    assertThat(sut.getUrl()).isEqualTo(STRING_UNIQUE2);
  }

  @Test public void testGetPreview() {
    assertThat(sut.getPreview()).isEqualTo(STRING_UNIQUE2);
  }

  @Test public void testSetPreview() {
    sut.setUrl(STRING_UNIQUE2);

    assertThat(sut.getPreview()).isEqualTo(STRING_UNIQUE2);
  }
}
