package burrows.apps.giphy.example.rest.model;

import org.junit.Test;
import test.TestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class DataTest extends TestBase {

    private final Images images = new Images();
    private final Data sut = new Data(this.images);

    @Test
    public void testGetImages() {
        assertThat(this.sut.getImages()).isEqualTo(images);
    }

    @Test
    public void testSetImages() {
        final Images expected = new Images();

        this.sut.setImages(expected);

        assertThat(this.sut.getImages()).isEqualTo(expected);
    }
}
