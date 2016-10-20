package burrows.apps.example.gif.data.rest.model;

import org.junit.Test;
import test.TestBase;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class ResultTest extends TestBase {
  private final List<Media> medias = new ArrayList<>();
  private final Result sut = new Result(medias, STRING_UNIQUE).withMedia(medias).withTitle(STRING_UNIQUE);

  @Test public void testGetMedia() {
    assertThat(sut.getMedia()).isEqualTo(medias);
  }

  @Test public void testSetMedia() {
    final List<Media> medias = new ArrayList<>();

    sut.setMedia(medias);

    assertThat(sut.getMedia()).isEqualTo(medias);
  }

  @Test public void testGetTitle() {
    assertThat(sut.getTitle()).isEqualTo(STRING_UNIQUE);
  }

  @Test public void testSetTitle() {
    sut.setTitle(STRING_UNIQUE2);

    assertThat(sut.getTitle()).isEqualTo(STRING_UNIQUE2);
  }
}
