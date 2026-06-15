package com.burrowsapps.gif.search.data.api.model

import com.burrowsapps.gif.search.di.NetworkModule
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class ResultDtoTest {
  private val gifDto = GifDto()
  private val mediaDto = MediaDto(tinyGif = gifDto, gif = gifDto)
  private val mediaList = listOf(mediaDto)
  private var sutDefault = ResultDto()
  private lateinit var sut: ResultDto

  internal lateinit var moshi: Moshi

  @Before
  fun setUp() {
    sut = ResultDto(media = mediaList)
    moshi = NetworkModule().provideMoshi()
  }

  @Test
  fun testGetMedia() {
    assertThat(sutDefault.media).isEmpty()
    assertThat(sut.media).isEqualTo(mediaList)
  }

  @Test
  fun testMoshiDeserialization_Positive() {
    val json =
      """
      {
        "media": [
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
        ]
      }
      """.trimIndent()

    val adapter = moshi.adapter(ResultDto::class.java)
    val result = adapter.fromJson(json)

    assertThat(result).isNotNull()
    assertThat(result?.media).hasSize(1)
    assertThat(result?.media?.first()).isEqualTo(mediaDto)
  }

  @Test
  fun testMoshiDeserialization_Negative() {
    val invalidJson =
      """
      {
        "media": [
          {
            "tinyGif": {
              "url": [],
              "preview": {}
            },
            "gif": null
          }
        ]
      }
      """.trimIndent()

    val adapter = moshi.adapter(ResultDto::class.java)

    assertThrows(JsonDataException::class.java) {
      adapter.fromJson(invalidJson)
    }
  }
}
