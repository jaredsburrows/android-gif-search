package burrows.apps.example.gif.data.rest.model;

import org.junit.Test;
import test.TestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class GifTest extends TestBase {
  private Gif sut = new Gif.Builder().url(STRING_UNIQUE).preview(STRING_UNIQUE2).build();

  @Test public void testGetUrl() {
    // Assert
    assertThat(sut.url()).isEqualTo(STRING_UNIQUE);
  }

  @Test public void testSetUrl() {
    // Arrange
    sut = sut.newBuilder().url(STRING_UNIQUE2).build();

    // Assert
    assertThat(sut.url()).isEqualTo(STRING_UNIQUE2);
  }

  @Test public void testGetPreview() {
    // Assert
    assertThat(sut.preview()).isEqualTo(STRING_UNIQUE2);
  }

  @Test public void testSetPreview() {
    // Arrange
    sut = sut.newBuilder().preview(STRING_UNIQUE3).build();

    // Assert
    assertThat(sut.preview()).isEqualTo(STRING_UNIQUE3);
  }
}
