package com.burrowsapps.gif.search.data.api.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaDtoTest {
  private val gifDto = GifDto()
  private var defaultSut = MediaDto()

  private lateinit var sut: MediaDto

  @Before
  fun setUp() {
    sut = MediaDto(gifDto)
  }

  @Test
  fun testTinyGetGif() {
    assertThat(defaultSut.tinyGif).isEqualTo(GifDto())
    assertThat(sut.tinyGif).isEqualTo(gifDto)
  }

  @Test
  fun testGetGif() {
    assertThat(defaultSut.gif).isEqualTo(GifDto())
    assertThat(sut.gif).isEqualTo(gifDto)
  }
}
