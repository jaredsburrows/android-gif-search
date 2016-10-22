package burrows.apps.example.gif.data.rest.model;

import org.junit.Test;
import test.TestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class MediaTest extends TestBase {
  private final Gif gif = new Gif();
  private Media sut = new Media.Builder().gif(gif).build();

  @Test public void testGetGif() {
    assertThat(sut.gif()).isEqualTo(gif);
  }

  @Test public void testSetGif() {
    final Gif expected = new Gif();

    sut = sut.newBuilder().gif(expected).build();

    assertThat(sut.gif()).isEqualTo(expected);
  }
}
