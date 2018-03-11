package burrows.apps.example.gif.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import test.TestBase

class ResultDtoTest : TestBase() {
  companion object {
    private val TEST_MEDIAS = arrayListOf<MediaDto>()
  }
  private var sutDefault = ResultDto()
  private var sut = ResultDto(media = TEST_MEDIAS, title = STRING_UNIQUE)

  @Test fun testGetMedia() {
    assertThat(sutDefault.media).isEmpty()
    assertThat(sut.media).isEqualTo(TEST_MEDIAS)
  }

  @Test fun testGetTitle() {
    assertThat(sutDefault.title).isEmpty()
    assertThat(sut.title).isEqualTo(STRING_UNIQUE)
  }
}
