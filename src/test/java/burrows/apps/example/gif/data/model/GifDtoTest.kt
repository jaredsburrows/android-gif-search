package burrows.apps.example.gif.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import test.TestBase

class GifDtoTest : TestBase() {
  private var defaultSut = GifDto()
  private var sut = GifDto(url = STRING_UNIQUE, preview = STRING_UNIQUE2)

  @Test fun testGetUrl() {
    assertThat(defaultSut.url).isEmpty()
    assertThat(sut.url).isEqualTo(STRING_UNIQUE)
  }

  @Test fun testGetPreview() {
    assertThat(defaultSut.url).isEmpty()
    assertThat(sut.preview).isEqualTo(STRING_UNIQUE2)
  }
}
