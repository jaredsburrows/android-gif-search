package burrows.apps.example.gif.data.rest.model;

import org.junit.Test;
import test.TestBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class RiffsyResponseTest extends TestBase {
  private final List<Result> data = new ArrayList<>(Collections.singletonList(new Result()));
  private RiffsyResponse sut = new RiffsyResponse.Builder().results(data).build();

  @Test public void testGetData() {
    assertThat(sut.results()).isEqualTo(data);
  }

  @Test public void testSetData() {
    final List<Result> expected = new ArrayList<>();

    sut = sut.newBuilder().results(expected).build();

    assertThat(sut.results()).isEqualTo(expected);
  }
}
