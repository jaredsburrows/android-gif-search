package com.burrowsapps.example.gif.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MediaDtoTest {
    private val gifDto = GifDto()
    private var defaultSut = MediaDto()
    private var sut = MediaDto(gifDto)

    @Test fun testGetGif() {
        assertThat(defaultSut.gif).isEqualTo(GifDto())
        assertThat(sut.gif).isEqualTo(gifDto)
    }
}
