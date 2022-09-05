package com.burrowsapps.example.gif.data.source.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResultDtoTest {
  private val mediaTitle = "Good Luck"
  private val media = listOf<MediaDto>()
  private var sutDefault = ResultDto()

  private lateinit var sut: ResultDto

  @Before
  fun setUp() {
    sut = ResultDto(media, mediaTitle)
  }

  @Test
  fun testGetMedia() {
    assertThat(sutDefault.media).isEmpty()
    assertThat(sut.media).isEqualTo(media)
  }

  @Test
  fun testGetTitle() {
    assertThat(sutDefault.title).isEmpty()
    assertThat(sut.title).isEqualTo(mediaTitle)
  }
}
