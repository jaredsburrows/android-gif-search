package burrows.apps.example.gif.giflist

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GifImageInfoTest {
  private val gifUrl = "https://media.riffsy.com/images/ed8cf447392c5e7e0cc16cbad2a0edce/tenor.gif"
  private val previewUrl = "https://media.riffsy.com/images/b1060f2602934944c0e3502a1d7d20d8/raw"
  private var sutDefault = GifImageInfo()
  private var sut = GifImageInfo(url = gifUrl, previewUrl = previewUrl)

  @Test fun testGetUrl() {
    assertThat(sutDefault.url).isEmpty()
    assertThat(sut.url).isEqualTo(gifUrl)
  }

  @Test fun testGetUrlPreview() {
    assertThat(sutDefault.previewUrl).isEmpty()
    assertThat(sut.previewUrl).isEqualTo(previewUrl)
  }
}
