package com.burrowsapps.example.gif.data.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RiffsyResponseDtoTest {
  private val nextResponse = 1.0
  private val results = arrayListOf<ResultDto>()
  private var sutDefault = RiffsyResponseDto()
  private var sut = RiffsyResponseDto(results, nextResponse)

  @Test fun testGetResults() {
    assertThat(sutDefault.results).isEmpty()
    assertThat(sut.results).isEqualTo(results)
  }

  @Test fun testGetNext() {
    assertThat(sutDefault.next).isEqualTo(0.0)
    assertThat(sut.next).isEqualTo(nextResponse)
  }
}
