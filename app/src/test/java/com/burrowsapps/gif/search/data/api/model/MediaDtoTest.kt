package com.burrowsapps.gif.search.data.api.model

import com.burrowsapps.gif.search.di.NetworkModule
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class MediaDtoTest {
  private val gifDto = GifDto()
  private var defaultSut = MediaDto()
  private lateinit var sut: MediaDto

  internal lateinit var moshi: Moshi

  @Before
  fun setUp() {
    sut = MediaDto(tinyGif = gifDto, gif = gifDto)
    moshi = NetworkModule().provideMoshi()
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

  @Test
  fun testMoshiDeserialization_Positive() {
    val json =
      """
      {
        "tinyGif": {
          "url": "${gifDto.url}",
          "preview": "${gifDto.preview}"
        },
        "gif": {
          "url": "${gifDto.url}",
          "preview": "${gifDto.preview}"
        }
      }
      """.trimIndent()

    val adapter = moshi.adapter(MediaDto::class.java)
    val result = adapter.fromJson(json)

    assertThat(result).isNotNull()
    assertThat(result?.tinyGif).isEqualTo(gifDto)
    assertThat(result?.gif).isEqualTo(gifDto)
  }

  @Test
  fun testMoshiDeserialization_Negative() {
    val invalidJson =
      """
      {
        "tinyGif": {
          "url": 123,
          "preview": true
        },
        "gif": {
          "url": null,
          "preview": {}
        }
      }
      """.trimIndent()

    val adapter = moshi.adapter(MediaDto::class.java)

    assertThrows(JsonDataException::class.java) {
      adapter.fromJson(invalidJson)
    }
  }
}
