package burrows.apps.example.gif.data.rest.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import test.TestBase

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class MediaTest : TestBase() {
  private val gif = Gif()
  private var sut = Media.Builder().gif(gif).build()

  @Test fun testGetGif() {
    // Assert
    assertThat(sut.gif()).isEqualTo(gif)
  }

  @Test fun testSetGif() {
    // Arrange
    val expected = Gif()

    // Act
    sut = sut.newBuilder().gif(expected).build()

    // Assert
    assertThat(sut.gif()).isEqualTo(expected)
  }
}
