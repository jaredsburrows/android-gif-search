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
  private RiffsyResponse sut = new RiffsyResponse.Builder().results(results).build();

  @Test public void testGetData() {
    assertThat(sut.results()).isEqualTo(results);
  }

  @Test public void testSetData() {
    final List<Result> expected = new ArrayList<>();

    sut = sut.newBuilder().results(expected).build();

    assertThat(sut.results()).isEqualTo(expected);
  }
}
