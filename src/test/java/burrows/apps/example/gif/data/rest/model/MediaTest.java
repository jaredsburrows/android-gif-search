package burrows.apps.example.gif.data.rest.model;

import org.junit.Test;
import test.TestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class MediaTest extends TestBase {
  private final Gif gif = new Gif();
  private final Media sut = new Media(gif).withGif(gif);

  @Test public void testGetGif() {
    assertThat(sut.getGif()).isEqualTo(gif);
  }

  @Test public void testSetGif() {
    final Gif expected = new Gif();

    sut.setGif(expected);

    assertThat(sut.getGif()).isEqualTo(expected);
  }
}
