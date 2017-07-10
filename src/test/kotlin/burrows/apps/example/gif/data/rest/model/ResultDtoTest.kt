package burrows.apps.example.gif.data.rest.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import test.TestBase

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class ResultDtoTest : TestBase() {
  private val testMedias = arrayListOf<MediaDto>()
  private var sut = ResultDto().apply { media = testMedias; title = STRING_UNIQUE }

  @Test fun testGetMedia() {
    assertThat(sut.media).isEqualTo(testMedias)
  }

  @Test fun testGetTitle() {
    assertThat(sut.title).isEqualTo(STRING_UNIQUE)
  }
}
