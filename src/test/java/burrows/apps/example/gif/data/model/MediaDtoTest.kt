package burrows.apps.example.gif.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MediaDtoTest {
  private val gifDto = GifDto()
  private var defaultSut = MediaDto()
  private var sut = MediaDto(gif = gifDto)

  @Test fun testGetGif() {
    assertThat(defaultSut.gif).isEqualTo(GifDto("", ""))
    assertThat(sut.gif).isEqualTo(gifDto)
  }
}
