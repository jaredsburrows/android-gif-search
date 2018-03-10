package burrows.apps.example.gif.giflist

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import test.TestBase

class GifImageInfoTest : TestBase() {
  private var sut = GifImageInfo(url = STRING_UNIQUE, previewUrl = STRING_UNIQUE2)

  @Test fun testGetUrl() {
    assertThat(sut.url).isEqualTo(STRING_UNIQUE)
  }

  @Test fun testGetUrlPreview() {
    assertThat(sut.previewUrl).isEqualTo(STRING_UNIQUE2)
  }
}
