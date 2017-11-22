package burrows.apps.example.gif.data.rest.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import test.TestBase

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class MediaDtoTest : TestBase() {
  companion object {
    private val TEST_GIF = GifDto()
  }
  private var sut = MediaDto().apply { gif = TEST_GIF }

  @Test fun testGetGif() {
    assertThat(sut.gif).isEqualTo(TEST_GIF)
  }
}
