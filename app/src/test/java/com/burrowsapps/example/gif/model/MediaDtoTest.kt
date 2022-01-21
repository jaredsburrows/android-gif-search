package com.burrowsapps.example.gif.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.example.gif.model.GifDto
import com.burrowsapps.example.gif.model.MediaDto
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaDtoTest {
  private val gifDto = GifDto()
  private var defaultSut = MediaDto()
  private var sut = MediaDto(gifDto)

  @Test
  fun testGetGif() {
    assertThat(defaultSut.gif).isEqualTo(GifDto())
    assertThat(sut.gif).isEqualTo(gifDto)
  }
}
