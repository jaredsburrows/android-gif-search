package com.burrowsapps.gif.search.data.api.model

import com.burrowsapps.gif.search.di.NetworkModule
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class MediaDtoTest {
  private val gifDto = GifDto(url = "https://static.klipy.com/a.gif")
  private val jpgDto = GifDto(url = "https://static.klipy.com/a.jpg")
  private var defaultSut = MediaDto()
  private lateinit var sut: MediaDto

  internal lateinit var moshi: Moshi

  @Before
  fun setUp() {
    sut = MediaDto(gif = gifDto, jpg = jpgDto)
    moshi = NetworkModule().provideMoshi()
  }

  @Test
  fun testGetGif() {
    assertThat(defaultSut.gif).isEqualTo(GifDto())
    assertThat(sut.gif).isEqualTo(gifDto)
  }

  @Test
  fun testGetJpg() {
    assertThat(defaultSut.jpg).isEqualTo(GifDto())
    assertThat(sut.jpg).isEqualTo(jpgDto)
  }

  @Test
  fun testMoshiDeserialization_Positive() {
    val json =
      """
      {
        "gif": {
          "url": "${gifDto.url}"
        },
        "jpg": {
          "url": "${jpgDto.url}"
        }
      }
      """.trimIndent()

    val adapter = moshi.adapter(MediaDto::class.java)
    val result = adapter.fromJson(json)

    assertThat(result).isNotNull()
    assertThat(result?.gif).isEqualTo(gifDto)
    assertThat(result?.jpg).isEqualTo(jpgDto)
  }

  @Test
  fun testMoshiDeserialization_Negative() {
    val invalidJson =
      """
      {
        "gif": {
          "url": 123
        },
        "jpg": {
          "url": {}
        }
      }
      """.trimIndent()

    val adapter = moshi.adapter(MediaDto::class.java)

    assertThrows(JsonDataException::class.java) {
      adapter.fromJson(invalidJson)
    }
  }
}
