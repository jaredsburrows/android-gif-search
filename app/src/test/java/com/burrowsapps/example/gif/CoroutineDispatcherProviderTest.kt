package com.burrowsapps.example.gif

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoroutineDispatcherProviderTest {
  private val sut = CoroutineDispatcherProvider()

  @Test fun testIo() {
    assertThat(sut.io()).isEqualTo(Dispatchers.IO)
  }

  @Test fun testUi() {
    assertThat(sut.ui()).isEqualTo(Dispatchers.Main)
  }
}
