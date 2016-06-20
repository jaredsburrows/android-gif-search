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
    private final GiphyResponse sut = new GiphyResponse(this.data);

    @Test
    public void testGetData() {
        assertThat(this.sut.getData()).isEqualTo(this.data);
    }

    @Test
    public void testSetData() {
        final List<Data> expected = new ArrayList<>();

        this.sut.setData(expected);

        assertThat(this.sut.getData()).isEqualTo(expected);

        final GiphyResponse sut = new GiphyResponse();

        assertThat(sut.getData()).isNotNull();
    }
}
