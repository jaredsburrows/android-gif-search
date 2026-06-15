package com.burrowsapps.gif.search.data.api.model

import com.burrowsapps.gif.search.di.NetworkModule
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class GifDtoTest {
  private val gifUrl = "https://media.tenor.com/images/ed8cf447392c5e7e0cc16cbad2a0edce/tenor.gif"
  private val previewUrl = "https://media.tenor.com/images/b1060f2602934944c0e3502a1d7d20d8/raw"
  private var defaultSut = GifDto()
  private lateinit var sut: GifDto

  internal lateinit var moshi: Moshi

  @Before
  fun setUp() {
    sut = GifDto(gifUrl, previewUrl)
    moshi = NetworkModule().provideMoshi()
  }

  @Test
  fun testGetUrl() {
    assertThat(defaultSut.url).isEmpty()
    assertThat(sut.url).isEqualTo(gifUrl)
  }

  @Test
  fun testGetPreview() {
    assertThat(defaultSut.preview).isEmpty()
    assertThat(sut.preview).isEqualTo(previewUrl)
  }

  @Test
  fun testMoshiDeserialization_Positive() {
    val json =
      """
      {
        "url": "$gifUrl",
        "preview": "$previewUrl"
      }
      """.trimIndent()

    val adapter = moshi.adapter(GifDto::class.java)
    val result = adapter.fromJson(json)

    assertThat(result).isNotNull()
    assertThat(result?.url).isEqualTo(gifUrl)
    assertThat(result?.preview).isEqualTo(previewUrl)
  }

  @Test
  fun testMoshiDeserialization_Negative() {
    val invalidJson =
      """
      {
        "url": 123,
        "preview": true
      }
      """.trimIndent()

    val adapter = moshi.adapter(GifDto::class.java)

    assertThrows(JsonDataException::class.java) {
      adapter.fromJson(invalidJson)
    }
  }
}
