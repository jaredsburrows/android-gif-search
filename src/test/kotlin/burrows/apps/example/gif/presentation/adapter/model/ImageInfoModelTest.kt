package burrows.apps.example.gif.presentation.adapter.model

import nl.jqno.equalsverifier.EqualsVerifier.forClass
import nl.jqno.equalsverifier.Warning
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import test.TestBase

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class ImageInfoModelTest : TestBase() {
  private var sut = ImageInfoModel().apply { url = STRING_UNIQUE; previewUrl = STRING_UNIQUE2 }

  @Test fun testGetUrl() {
    // Assert
    assertThat(sut.url).isEqualTo(STRING_UNIQUE)
  }

  @Test fun testGetUrlPreview() {
    // Assert
    assertThat(sut.previewUrl).isEqualTo(STRING_UNIQUE2)
  }

  @Test fun testEqualsGashCode() {
    // Assert
    forClass(ImageInfoModel::class.java)
      .withPrefabValues(ImageInfoModel::class.java, ImageInfoModel(), ImageInfoModel().apply { url = STRING_UNIQUE })
      .suppress(Warning.NONFINAL_FIELDS)
      .verify()
  }
}
