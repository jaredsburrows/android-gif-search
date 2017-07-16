package burrows.apps.example.gif.data.rest.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import test.TestBase

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class MediaDtoTest : TestBase() {
  private val TEST_GIF = GifDto()
  private var sut = MediaDto().apply { gif = TEST_GIF }

  @Test fun testGetGif() {
    assertThat(sut.gif).isEqualTo(TEST_GIF)
  }
}
