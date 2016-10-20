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
public class RiffsyResponseTest extends TestBase {
  private final List<Result> data = new ArrayList<>(Collections.singletonList(new Result()));
  private final RiffsyResponse sut = new RiffsyResponse(data).withGif(data);

  @Test public void testGetData() {
    assertThat(sut.getResults()).isEqualTo(data);
  }

  @Test public void testSetData() {
    final List<Result> expected = new ArrayList<>();

    sut.setResults(expected);

    assertThat(sut.getResults()).isEqualTo(expected);

    final RiffsyResponse sut = new RiffsyResponse();
    assertThat(sut.getResults()).isNotNull();
  }
}
