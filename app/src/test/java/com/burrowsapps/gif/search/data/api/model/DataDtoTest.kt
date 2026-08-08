package com.burrowsapps.gif.search.data.api.model

import com.burrowsapps.gif.search.di.NetworkModule
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class DataDtoTest {
  private val results = listOf(ResultDto())
  private var sutDefault = DataDto()
  private lateinit var sut: DataDto

  internal lateinit var moshi: Moshi

  @Before
  fun setUp() {
    sut = DataDto(results = results, currentPage = 2, perPage = 1, hasNext = true)
    moshi = NetworkModule().provideMoshi()
  }

  @Test
  fun testGetResults() {
    assertThat(sutDefault.results).isEmpty()
    assertThat(sut.results).isEqualTo(results)
  }

  @Test
  fun testGetCurrentPage() {
    assertThat(sutDefault.currentPage).isEqualTo(1)
    assertThat(sut.currentPage).isEqualTo(2)
  }

  @Test
  fun testGetHasNext() {
    assertThat(sutDefault.hasNext).isFalse()
    assertThat(sut.hasNext).isTrue()
  }

  @Test
  fun testMoshiDeserialization_Positive() {
    val json =
      """
      {
        "data": [
          {
            "file": {}
          }
        ],
        "current_page": 2,
        "per_page": 1,
        "has_next": true
      }
      """.trimIndent()

    val adapter = moshi.adapter(DataDto::class.java)
    val result = adapter.fromJson(json)

    assertThat(result).isNotNull()
    assertThat(result?.results).hasSize(1)
    assertThat(result?.currentPage).isEqualTo(2)
    assertThat(result?.perPage).isEqualTo(1)
    assertThat(result?.hasNext).isTrue()
  }

  @Test
  fun testMoshiDeserialization_Negative() {
    val invalidJson =
      """
      {
        "data": 42,
        "current_page": [],
        "has_next": {}
      }
      """.trimIndent()

    val adapter = moshi.adapter(DataDto::class.java)

    assertThrows(JsonDataException::class.java) {
      adapter.fromJson(invalidJson)
    }
  }
}
