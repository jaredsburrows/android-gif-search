package burrows.apps.giphy.example.rest.model;

import org.junit.Test;
import test.TestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class FixedHeightTest extends TestBase {
    private FixedHeight sut = new FixedHeight(STRING_UNIQUE);

    @Test public void testGetUrl() {
        assertThat(this.sut.getUrl()).isEqualTo(STRING_UNIQUE);
    }

    @Test public void testSetUrl() {
        this.sut.setUrl(STRING_UNIQUE2);

        assertThat(this.sut.getUrl()).isEqualTo(STRING_UNIQUE2);
    }
}
