package burrows.apps.giphy.example.rest.model;

import org.junit.Test;
import test.TestBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class GiphyResponseTest extends TestBase {
  private final List<Data> data = new ArrayList<>(Collections.singletonList(new Data()));
  private GiphyResponse sut = new GiphyResponse(data);

  @Test public void testGetData() {
    assertThat(sut.getData()).isEqualTo(data);
  }

  @Test public void testSetData() {
    final List<Data> expected = new ArrayList<>();

    sut.setData(expected);

    assertThat(sut.getData()).isEqualTo(expected);

    final GiphyResponse sut = new GiphyResponse();

    assertThat(sut.getData()).isNotNull();
  }
}
