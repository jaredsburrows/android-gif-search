package com.burrowsapps.gif.search.data.api.model

import com.burrowsapps.gif.search.di.NetworkModule
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class GifResponseDtoTest {
  private val gifDto = GifDto(url = "https://static.klipy.com/sm.gif")
  private val jpgDto = GifDto(url = "https://static.klipy.com/sm.jpg")
  private val mediaDto = MediaDto(gif = gifDto, jpg = jpgDto)
  private val resultDto = ResultDto(file = FileDto(md = mediaDto, sm = mediaDto))
  private val dataDto = DataDto(results = listOf(resultDto), currentPage = 1, perPage = 1, hasNext = true)
  private var sutDefault = GifResponseDto()
  private lateinit var sut: GifResponseDto

  internal lateinit var moshi: Moshi

  @Before
  fun setUp() {
    sut = GifResponseDto(result = true, data = dataDto)
    moshi = NetworkModule().provideMoshi()
  }

  @Test
  fun testGetResult() {
    assertThat(sutDefault.result).isFalse()
    assertThat(sut.result).isTrue()
  }

  @Test
  fun testGetData() {
    assertThat(sutDefault.data).isEqualTo(DataDto())
    assertThat(sut.data).isEqualTo(dataDto)
  }

  @Test
  fun testMoshiDeserialization_Positive() {
    val json =
      """
      {
        "result": true,
        "data": {
          "data": [
            {
              "file": {
                "md": {
                  "gif": { "url": "${gifDto.url}" },
                  "jpg": { "url": "${jpgDto.url}" }
                },
                "sm": {
                  "gif": { "url": "${gifDto.url}" },
                  "jpg": { "url": "${jpgDto.url}" }
                }
              }
            }
          ],
          "current_page": 1,
          "per_page": 1,
          "has_next": true
        }
      }
      """.trimIndent()

    val adapter = moshi.adapter(GifResponseDto::class.java)
    val result = adapter.fromJson(json)

    assertThat(result).isNotNull()
    assertThat(result?.result).isTrue()
    assertThat(result?.data?.results).hasSize(1)
    assertThat(result?.data?.results?.first()).isEqualTo(resultDto)
    assertThat(result?.data?.hasNext).isTrue()
  }

  @Test
  fun testMoshiDeserialization_Negative() {
    val invalidJson =
      """
      {
        "result": "yes",
        "data": {
          "data": [
            {
              "file": 42
            }
          ],
          "current_page": false,
          "has_next": []
        }
      }
      """.trimIndent()

    val adapter = moshi.adapter(GifResponseDto::class.java)

    assertThrows(JsonDataException::class.java) {
      adapter.fromJson(invalidJson)
    }
  }
}
