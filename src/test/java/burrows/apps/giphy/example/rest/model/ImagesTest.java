package burrows.apps.giphy.example.rest.model;

import org.junit.Test;
import test.TestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class ImagesTest extends TestBase {
  private final FixedHeight fixedHeight = new FixedHeight();
  private Images sut = new Images(fixedHeight);

  @Test public void testGetFixedHeight() {
    assertThat(sut.getFixedHeight()).isEqualTo(fixedHeight);
  }

  @Test public void testSetFixedHeight() {
    final FixedHeight expected = new FixedHeight();

    sut.setFixedHeight(expected);

    assertThat(sut.getFixedHeight()).isEqualTo(expected);
  }
}
