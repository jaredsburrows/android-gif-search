package com.burrowsapps.gif.search.data.api.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResultDtoTest {
  private val media = listOf<MediaDto>()
  private var sutDefault = ResultDto()

  private lateinit var sut: ResultDto

  @Before
  fun setUp() {
    sut = ResultDto(media)
  }

  @Test
  fun testGetMedia() {
    assertThat(sutDefault.media).isEmpty()
    assertThat(sut.media).isEqualTo(media)
  }
}
