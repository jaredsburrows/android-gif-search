package burrows.apps.example.gif.data.rest.model;

import org.junit.Test;
import test.TestBase;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class RiffsyResponseTest extends TestBase {
  private List<Result> results = new ArrayList<>();
  private RiffsyResponse sut = new RiffsyResponse.Builder().results(results).page(FLOAT_RANDOM).build();

  @Test public void testGetResults() {
    // Assert
    assertThat(sut.results()).isEqualTo(results);
  }

  @Test public void testSetResults() {
    // Arrange
    final List<Result> expected = new ArrayList<>();

    // Act
    sut = sut.newBuilder().results(expected).build();

    // Assert
    assertThat(sut.results()).isEqualTo(expected);
  }

  @Test public void testGetNext() {
    // Assert
    assertThat(sut.page()).isEqualTo(FLOAT_RANDOM);
  }

  @Test public void testSetNext() {
    // Act
    sut = sut.newBuilder().page(FLOAT_RANDOM + 1).build();

    // Assert
    assertThat(sut.page()).isEqualTo(FLOAT_RANDOM + 1);
  }
}
