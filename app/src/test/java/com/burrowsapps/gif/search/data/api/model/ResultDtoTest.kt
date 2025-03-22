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
class ResultDtoTest {
  private val gifDto = GifDto()
  private val mediaDto = MediaDto(tinyGif = gifDto, gif = gifDto)
  private val mediaList = listOf(mediaDto)
  private var sutDefault = ResultDto()
  private lateinit var sut: ResultDto

  @get:Rule(order = 0)
  internal val hiltRule = HiltAndroidRule(this)

  @Inject
  internal lateinit var moshi: Moshi

  @Before
  fun setUp() {
    hiltRule.inject()
    sut = ResultDto(media = mediaList)
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
