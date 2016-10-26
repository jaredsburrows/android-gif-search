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
  private RiffsyResponse sut = new RiffsyResponse.Builder().results(results).next(FLOAT_RANDOM).build();

  @Test public void testGetResults() {
    assertThat(sut.results()).isEqualTo(results);
  }

  @Test public void testSetResults() {
    final List<Result> expected = new ArrayList<>();

    sut = sut.newBuilder().results(expected).build();

    assertThat(sut.results()).isEqualTo(expected);
  }

  @Test public void testGetNext() {
    assertThat(sut.next()).isEqualTo(FLOAT_RANDOM);
  }

  @Test public void testSetNext() {
    sut = sut.newBuilder().next(FLOAT_RANDOM + 1).build();

    assertThat(sut.next()).isEqualTo(FLOAT_RANDOM + 1);
  }
}
