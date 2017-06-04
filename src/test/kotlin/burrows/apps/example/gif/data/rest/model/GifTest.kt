package burrows.apps.example.gif.data.rest.model

import org.junit.Test
import test.TestBase

import org.assertj.core.api.Assertions.assertThat

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class GifTest : TestBase() {
    private var sut = Gif.Builder().url(TestBase.STRING_UNIQUE).preview(TestBase.STRING_UNIQUE2).build()

    @Test fun testGetUrl() {
        // Assert
        assertThat(sut.url()).isEqualTo(TestBase.STRING_UNIQUE)
    }

    @Test fun testSetUrl() {
        // Arrange
        sut = sut.newBuilder().url(TestBase.STRING_UNIQUE2).build()

        // Assert
        assertThat(sut.url()).isEqualTo(TestBase.STRING_UNIQUE2)
    }

    @Test fun testGetPreview() {
        // Assert
        assertThat(sut.preview()).isEqualTo(TestBase.STRING_UNIQUE2)
    }

    @Test fun testSetPreview() {
        // Arrange
        sut = sut.newBuilder().preview(TestBase.STRING_UNIQUE3).build()

        // Assert
        assertThat(sut.preview()).isEqualTo(TestBase.STRING_UNIQUE3)
    }
}
