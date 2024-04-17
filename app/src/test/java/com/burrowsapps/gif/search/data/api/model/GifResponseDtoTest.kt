package com.burrowsapps.gif.search.data.api.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifResponseDtoTest {
  private val nextResponse = "1.0"
  private val results = listOf<ResultDto>()
  private var sutDefault = GifResponseDto()

  private lateinit var sut: GifResponseDto

  @Before
  fun setUp() {
    sut = GifResponseDto(results, nextResponse)
  }

  @Test
  fun testGetResults() {
    assertThat(sutDefault.results).isEmpty()
    assertThat(sut.results).isEqualTo(results)
  }

  @Test
  fun testGetNext() {
    assertThat(sutDefault.next).isEqualTo("0.0")
    assertThat(sut.next).isEqualTo(nextResponse)
  }
}
