package burrows.apps.example.gif.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ResultDtoTest {
  private val mediaTitle = "Good Luck"
  private val media = arrayListOf<MediaDto>()
  private var sutDefault = ResultDto()
  private var sut = ResultDto(media = media, title = mediaTitle)

  @Test fun testGetMedia() {
    assertThat(sutDefault.media).isEmpty()
    assertThat(sut.media).isEqualTo(media)
  }

  @Test fun testGetTitle() {
    assertThat(sutDefault.title).isEmpty()
    assertThat(sut.title).isEqualTo(mediaTitle)
  }
}
