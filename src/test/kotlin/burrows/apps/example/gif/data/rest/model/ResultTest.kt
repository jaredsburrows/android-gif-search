package burrows.apps.example.gif.data.rest.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import test.TestBase

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class ResultTest : TestBase() {
  private val medias = ArrayList<Media>()
  private var sut = Result.Builder().media(medias).title(TestBase.STRING_UNIQUE).build()

  @Test fun testGetMedia() {
    // Assert
    assertThat(sut.media()).isEqualTo(medias)
  }

  @Test fun testSetMedia() {
    // Arrange
    val medias = ArrayList<Media>()

    // Act
    sut = sut.newBuilder().media(medias).build()

    // Assert
    assertThat(sut.media()).isEqualTo(medias)
  }

  @Test fun testGetTitle() {
    // Assert
    assertThat(sut.title()).isEqualTo(TestBase.STRING_UNIQUE)
  }

  @Test fun testSetTitle() {
    // Act
    sut = sut.newBuilder().title(TestBase.STRING_UNIQUE2).build()

    // Assert
    assertThat(sut.title()).isEqualTo(TestBase.STRING_UNIQUE2)
  }
}
