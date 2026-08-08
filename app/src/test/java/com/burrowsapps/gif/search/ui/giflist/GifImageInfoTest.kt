package com.burrowsapps.gif.search.ui.giflist

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class GifImageInfoTest {
  private val gifUrl = "https://static.klipy.com/ii/c3a19a0b747a76e98651f2b9a3cca5ff/a7/68/IkPBE6uB.gif"
  private val previewUrl = "https://static.klipy.com/ii/c3a19a0b747a76e98651f2b9a3cca5ff/a7/68/idSbgVOfzB3KVfKSzd8W.jpg"
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
