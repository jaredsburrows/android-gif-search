package burrows.apps.example.gif.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import test.TestBase

class GifDtoTest : TestBase() {
  private var sut = GifDto(url = STRING_UNIQUE, preview = STRING_UNIQUE2)

  @Test fun testGetUrl() {
    assertThat(sut.url).isEqualTo(STRING_UNIQUE)
  }

  @Test fun testGetPreview() {
    assertThat(sut.preview).isEqualTo(STRING_UNIQUE2)
  }
}
