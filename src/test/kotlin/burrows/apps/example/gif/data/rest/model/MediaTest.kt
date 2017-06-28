package burrows.apps.example.gif.data.rest.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import test.TestBase

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class MediaTest : TestBase() {
  private val gifTest = Gif()
  private var sut = Media().apply { gif = gifTest }

  @Test fun testGetGif() {
    // Assert
    assertThat(sut.gif).isEqualTo(gifTest)
  }
}
