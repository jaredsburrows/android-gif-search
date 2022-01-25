package com.burrowsapps.example.gif.data.source.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TenorResponseDtoTest {
  private val nextResponse = "1.0"
  private val results = listOf<ResultDto>()
  private var sutDefault = TenorResponseDto()
  private var sut = TenorResponseDto(results, nextResponse)

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
