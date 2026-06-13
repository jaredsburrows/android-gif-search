package com.burrowsapps.gif.search.ui.giflist

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class GifImageInfoTest {
  private val gifUrl = "https://media.tenor.com/images/ed8cf447392c5e7e0cc16cbad2a0edce/tenor.gif"
  private val previewUrl = "https://media.tenor.com/images/b1060f2602934944c0e3502a1d7d20d8/raw"
  private var sutDefault = GifImageInfo()

  private lateinit var sut: GifImageInfo

  @Before
  fun setUp() {
    sut =
      GifImageInfo(
        tinyGifUrl = gifUrl,
        tinyGifPreviewUrl = previewUrl,
        gifUrl = gifUrl,
        gifPreviewUrl = previewUrl,
      )
  }

  @Test
  fun testGetTinyURL() {
    assertThat(sutDefault.tinyGifUrl).isEmpty()
    assertThat(sut.tinyGifUrl).isEqualTo(gifUrl)
  }

  @Test
  fun testGetTinyURLPreview() {
    assertThat(sutDefault.tinyGifPreviewUrl).isEmpty()
    assertThat(sut.tinyGifPreviewUrl).isEqualTo(previewUrl)
  }

  @Test
  fun testGetURL() {
    assertThat(sutDefault.gifUrl).isEmpty()
    assertThat(sut.gifUrl).isEqualTo(gifUrl)
  }

  @Test
  fun testGetURLPreview() {
    assertThat(sutDefault.gifPreviewUrl).isEmpty()
    assertThat(sut.gifPreviewUrl).isEqualTo(previewUrl)
  }
}
