package burrows.apps.example.gif.data.rest.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import test.TestBase

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class GifDtoTest : TestBase() {
  private var sut = GifDto().apply { url = STRING_UNIQUE; preview = STRING_UNIQUE2 }

  @Test fun testGetUrl() {
    assertThat(sut.url).isEqualTo(STRING_UNIQUE)
  }

  @Test fun testGetPreview() {
    assertThat(sut.preview).isEqualTo(STRING_UNIQUE2)
  }
}
