package com.burrowsapps.gif.search.data.api.model

import com.burrowsapps.gif.search.di.NetworkModule
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class FileDtoTest {
  private val mdDto =
    MediaDto(
      gif = GifDto(url = "https://static.klipy.com/md.gif"),
      jpg = GifDto(url = "https://static.klipy.com/md.jpg"),
    )
  private val smDto =
    MediaDto(
      gif = GifDto(url = "https://static.klipy.com/sm.gif"),
      jpg = GifDto(url = "https://static.klipy.com/sm.jpg"),
    )
  private var defaultSut = FileDto()
  private lateinit var sut: FileDto

  internal lateinit var moshi: Moshi

  @Before
  fun setUp() {
    sut = FileDto(md = mdDto, sm = smDto)
    moshi = NetworkModule().provideMoshi()
  }

  @Test
  fun testGetMd() {
    assertThat(defaultSut.md).isEqualTo(MediaDto())
    assertThat(sut.md).isEqualTo(mdDto)
  }

  @Test
  fun testGetSm() {
    assertThat(defaultSut.sm).isEqualTo(MediaDto())
    assertThat(sut.sm).isEqualTo(smDto)
  }

  @Test
  fun testMoshiDeserialization_Positive() {
    val json =
      """
      {
        "md": {
          "gif": { "url": "${mdDto.gif.url}" },
          "jpg": { "url": "${mdDto.jpg.url}" }
        },
        "sm": {
          "gif": { "url": "${smDto.gif.url}" },
          "jpg": { "url": "${smDto.jpg.url}" }
        }
      }
      """.trimIndent()

    val adapter = moshi.adapter(FileDto::class.java)
    val result = adapter.fromJson(json)

    assertThat(result).isNotNull()
    assertThat(result?.md).isEqualTo(mdDto)
    assertThat(result?.sm).isEqualTo(smDto)
  }

  @Test
  fun testMoshiDeserialization_Negative() {
    val invalidJson =
      """
      {
        "md": 42,
        "sm": []
      }
      """.trimIndent()

    val adapter = moshi.adapter(FileDto::class.java)

    assertThrows(JsonDataException::class.java) {
      adapter.fromJson(invalidJson)
    }
  }
}
