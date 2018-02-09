package burrows.apps.example.gif.presentation.adapter.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import test.TestBase

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class ImageInfoModelTest : TestBase() {
  private var sut = ImageInfoModel().apply { url = STRING_UNIQUE; previewUrl = STRING_UNIQUE2 }

  @Test fun testGetUrl() {
    assertThat(sut.url).isEqualTo(STRING_UNIQUE)
  }

  @Test fun testGetUrlPreview() {
    assertThat(sut.previewUrl).isEqualTo(STRING_UNIQUE2)
  }
}
