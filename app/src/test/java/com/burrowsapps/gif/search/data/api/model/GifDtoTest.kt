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
class GifDtoTest {
  private val gifUrl = "https://media.tenor.com/images/ed8cf447392c5e7e0cc16cbad2a0edce/tenor.gif"
  private val previewUrl = "https://media.tenor.com/images/b1060f2602934944c0e3502a1d7d20d8/raw"
  private var defaultSut = GifDto()
  private lateinit var sut: GifDto

  @get:Rule(order = 0)
  internal val hiltRule = HiltAndroidRule(this)

  @Inject
  internal lateinit var moshi: Moshi

  @Before
  fun setUp() {
    hiltRule.inject()

    sut = GifDto(gifUrl, previewUrl)
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
