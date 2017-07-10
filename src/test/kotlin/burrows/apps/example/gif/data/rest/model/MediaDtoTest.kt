package burrows.apps.example.gif.data.rest.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import test.TestBase

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class MediaDtoTest : TestBase() {
  private val gifTest = GifDto()
  private var sut = MediaDto().apply { gif = gifTest }

  @Test fun testGetGif() {
    assertThat(sut.gif).isEqualTo(gifTest)
  }
}
