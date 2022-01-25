package com.burrowsapps.example.gif.ui.giflist

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifImageInfoTest {
  private val gifUrl = "https://media.tenor.com/images/ed8cf447392c5e7e0cc16cbad2a0edce/tenor.gif"
  private val previewUrl = "https://media.tenor.com/images/b1060f2602934944c0e3502a1d7d20d8/raw"
  private var sutDefault = GifImageInfo()
  private var sut = GifImageInfo(gifUrl, previewUrl, gifUrl, previewUrl)

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
