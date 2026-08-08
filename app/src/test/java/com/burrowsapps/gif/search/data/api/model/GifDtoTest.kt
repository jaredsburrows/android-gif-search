package com.burrowsapps.gif.search.data.api.model

import com.burrowsapps.gif.search.di.NetworkModule
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class GifDtoTest {
  private val gifUrl = "https://static.klipy.com/ii/c3a19a0b747a76e98651f2b9a3cca5ff/a7/68/IkPBE6uB.gif"
  private var defaultSut = GifDto()
  private lateinit var sut: GifDto

  internal lateinit var moshi: Moshi

  @Before
  fun setUp() {
    sut = GifDto(gifUrl)
    moshi = NetworkModule().provideMoshi()
  }

  @Test
  fun testGetUrl() {
    assertThat(defaultSut.url).isEmpty()
    assertThat(sut.url).isEqualTo(gifUrl)
  }

  @Test
  fun testMoshiDeserialization_Positive() {
    val json =
      """
      {
        "url": "$gifUrl"
      }
      """.trimIndent()

    val adapter = moshi.adapter(GifDto::class.java)
    val result = adapter.fromJson(json)

    assertThat(result).isNotNull()
    assertThat(result?.url).isEqualTo(gifUrl)
  }

  @Test
  fun testMoshiDeserialization_Negative() {
    val invalidJson =
      """
      {
        "url": true
      }
      """.trimIndent()

    val adapter = moshi.adapter(GifDto::class.java)

    assertThrows(JsonDataException::class.java) {
      adapter.fromJson(invalidJson)
    }
  }
}
