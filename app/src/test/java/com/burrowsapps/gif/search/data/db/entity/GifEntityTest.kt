package com.burrowsapps.gif.search.data.db.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GifEntityTest {
  @Test
  fun fields_areAssigned() {
    val entity =
      GifEntity(
        tinyGifUrl = "tiny",
        tinyGifPreviewUrl = "tiny-preview",
        gifUrl = "gif",
        gifPreviewUrl = "gif-preview",
      )

    assertThat(entity.tinyGifUrl).isEqualTo("tiny")
    assertThat(entity.tinyGifPreviewUrl).isEqualTo("tiny-preview")
    assertThat(entity.gifUrl).isEqualTo("gif")
    assertThat(entity.gifPreviewUrl).isEqualTo("gif-preview")
  }
}
