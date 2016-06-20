package burrows.apps.giphy.example.rest.model;

import org.junit.Test;
import test.TestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class ImagesTest extends TestBase {

    private final FixedHeight fixedHeight = new FixedHeight();
    private final Images sut = new Images(this.fixedHeight);

    @Test
    public void testGetFixedHeight() {
        assertThat(this.sut.getFixedHeight()).isEqualTo(this.fixedHeight);
    }

    @Test
    public void testSetFixedHeight() {
        final FixedHeight expected = new FixedHeight();

        this.sut.setFixedHeight(expected);

        assertThat(this.sut.getFixedHeight()).isEqualTo(expected);
    }
}
