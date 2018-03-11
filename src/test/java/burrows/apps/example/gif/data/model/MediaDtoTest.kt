package burrows.apps.example.gif.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import test.TestBase

class MediaDtoTest : TestBase() {
  companion object {
    private val TEST_GIF = GifDto()
  }
  private var defaultSut = MediaDto()
  private var sut = MediaDto(gif = TEST_GIF)

  @Test fun testGetGif() {
    assertThat(defaultSut.gif).isEqualTo(GifDto("", ""))
    assertThat(sut.gif).isEqualTo(TEST_GIF)
  }
}
