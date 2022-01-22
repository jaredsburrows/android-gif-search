package com.burrowsapps.example.gif.data.source.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifDtoTest {
  private val gifUrl = "https://media.tenor.com/images/ed8cf447392c5e7e0cc16cbad2a0edce/tenor.gif"
  private val previewUrl = "https://media.tenor.com/images/b1060f2602934944c0e3502a1d7d20d8/raw"
  private var defaultSut = GifDto()
  private var sut = GifDto(gifUrl, previewUrl)

  @Test
  fun testGetUrl() {
    assertThat(defaultSut.url).isEmpty()
    assertThat(sut.url).isEqualTo(gifUrl)
  }

  @Test
  fun testGetPreview() {
    assertThat(defaultSut.url).isEmpty()
    assertThat(sut.preview).isEqualTo(previewUrl)
  }
}
