package burrows.apps.example.gif.data.rest.model;

import org.junit.Test;
import test.TestBase;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class ResultTest extends TestBase {
  private List<Media> medias = new ArrayList<>();
  private Result sut = new Result.Builder().media(medias).title(STRING_UNIQUE).build();

  @Test public void testGetMedia() {
    // Assert
    assertThat(sut.media()).isEqualTo(medias);
  }

  @Test public void testSetMedia() {
    // Arrange
    final List<Media> medias = new ArrayList<>();

    // Act
    sut = sut.newBuilder().media(medias).build();

    // Assert
    assertThat(sut.media()).isEqualTo(medias);
  }

  @Test public void testGetTitle() {
    // Assert
    assertThat(sut.title()).isEqualTo(STRING_UNIQUE);
  }

  @Test public void testSetTitle() {
    // Act
    sut = sut.newBuilder().title(STRING_UNIQUE2).build();

    // Assert
    assertThat(sut.title()).isEqualTo(STRING_UNIQUE2);
  }
}
