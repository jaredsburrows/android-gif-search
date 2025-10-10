package com.burrowsapps.gif.search.data.db.entity

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifEntityTest {
  @Test
  fun fields_areAssigned() {
    val entity =
      GifEntity(
        tinyGifUrl = "tiny",
        tinyGifPreviewUrl = "tinyp",
        gifUrl = "gif",
        gifPreviewUrl = "gifp",
      )

    assertThat(entity.tinyGifUrl).isEqualTo("tiny")
    assertThat(entity.tinyGifPreviewUrl).isEqualTo("tinyp")
    assertThat(entity.gifUrl).isEqualTo("gif")
    assertThat(entity.gifPreviewUrl).isEqualTo("gifp")
  }
}
