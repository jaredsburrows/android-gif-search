package com.burrowsapps.gif.search.data.api.model

import com.burrowsapps.gif.search.di.NetworkModule
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class ResultDtoTest {
  private val fileDto =
    FileDto(
      md =
        MediaDto(
          gif = GifDto(url = "https://static.klipy.com/md.gif"),
          jpg = GifDto(url = "https://static.klipy.com/md.jpg"),
        ),
      sm =
        MediaDto(
          gif = GifDto(url = "https://static.klipy.com/sm.gif"),
          jpg = GifDto(url = "https://static.klipy.com/sm.jpg"),
        ),
    )
  private var sutDefault = ResultDto()
  private lateinit var sut: ResultDto

  internal lateinit var moshi: Moshi

  @Before
  fun setUp() {
    sut = ResultDto(file = fileDto)
    moshi = NetworkModule().provideMoshi()
  }

  @Test
  fun testGetFile() {
    assertThat(sutDefault.file).isEqualTo(FileDto())
    assertThat(sut.file).isEqualTo(fileDto)
  }

  @Test
  fun testMoshiDeserialization_Positive() {
    val json =
      """
      {
        "file": {
          "md": {
            "gif": { "url": "${fileDto.md.gif.url}" },
            "jpg": { "url": "${fileDto.md.jpg.url}" }
          },
          "sm": {
            "gif": { "url": "${fileDto.sm.gif.url}" },
            "jpg": { "url": "${fileDto.sm.jpg.url}" }
          }
        }
      }
      """.trimIndent()

    val adapter = moshi.adapter(ResultDto::class.java)
    val result = adapter.fromJson(json)

    assertThat(result).isNotNull()
    assertThat(result?.file).isEqualTo(fileDto)
  }

  @Test
  fun testMoshiDeserialization_Negative() {
    val invalidJson =
      """
      {
        "file": {
          "md": {
            "gif": { "url": [] },
            "jpg": null
          },
          "sm": 42
        }
      }
      """.trimIndent()

    val adapter = moshi.adapter(ResultDto::class.java)

    assertThrows(JsonDataException::class.java) {
      adapter.fromJson(invalidJson)
    }
  }
}
