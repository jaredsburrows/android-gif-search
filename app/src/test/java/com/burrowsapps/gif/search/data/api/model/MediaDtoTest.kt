package com.burrowsapps.gif.search.data.api.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.di.ApiConfigModule
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import jakarta.inject.Inject
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@HiltAndroidTest
@UninstallModules(ApiConfigModule::class)
@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
class MediaDtoTest {
  private val gifDto = GifDto()
  private var defaultSut = MediaDto()
  private lateinit var sut: MediaDto

  @get:Rule(order = 0)
  internal val hiltRule = HiltAndroidRule(this)

  @Inject
  internal lateinit var moshi: Moshi

  @Before
  fun setUp() {
    hiltRule.inject()
    sut = MediaDto(tinyGif = gifDto, gif = gifDto)
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
