package com.burrowsapps.gif.search.data.api.model

import com.burrowsapps.gif.search.di.NetworkModule
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class GifResponseDtoTest {
  private val gifDto = GifDto()
  private val mediaDto = MediaDto(tinyGif = gifDto, gif = gifDto)
  private val resultDto = ResultDto(media = listOf(mediaDto))
  private val results = listOf(resultDto)
  private val nextResponse = "1.0"
  private var sutDefault = GifResponseDto()
  private lateinit var sut: GifResponseDto

  internal lateinit var moshi: Moshi

  @Before
  fun setUp() {
    sut = GifResponseDto(results = results, next = nextResponse)
    moshi = NetworkModule().provideMoshi()
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

  @Test
  fun testMoshiDeserialization_Positive() {
    val json =
      """
      {
        "results": [
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
        ],
        "next": "$nextResponse"
      }
      """.trimIndent()

    val adapter = moshi.adapter(GifResponseDto::class.java)
    val result = adapter.fromJson(json)

    assertThat(result).isNotNull()
    assertThat(result?.results).hasSize(1)
    assertThat(result?.results?.first()).isEqualTo(resultDto)
    assertThat(result?.next).isEqualTo(nextResponse)
  }

  @Test
  fun testMoshiDeserialization_Negative() {
    val invalidJson =
      """
      {
        "results": [
          {
            "media": [
              {
                "tinyGif": {
                  "url": {},
                  "preview": []
                },
                "gif": 42
              }
            ]
          }
        ],
        "next": false
      }
      """.trimIndent()

    val adapter = moshi.adapter(GifResponseDto::class.java)

    assertThrows(JsonDataException::class.java) {
      adapter.fromJson(invalidJson)
    }
  }
}
