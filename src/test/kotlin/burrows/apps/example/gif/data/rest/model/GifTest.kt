package burrows.apps.example.gif.data.rest.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import test.TestBase

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class GifTest : TestBase() {
  private var sut = Gif().apply { url = TestBase.STRING_UNIQUE; preview = TestBase.STRING_UNIQUE2 }

  @Test fun testGetUrl() {
    // Assert
    assertThat(sut.url).isEqualTo(TestBase.STRING_UNIQUE)
  }

  @Test fun testGetPreview() {
    // Assert
    assertThat(sut.preview).isEqualTo(TestBase.STRING_UNIQUE2)
  }
}
