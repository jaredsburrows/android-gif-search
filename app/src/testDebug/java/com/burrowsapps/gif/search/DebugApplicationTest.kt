package com.burrowsapps.gif.search

import android.os.strictmode.IncorrectContextUseViolation
import android.os.strictmode.LeakedClosableViolation
import android.os.strictmode.UntaggedSocketViolation
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock

@RunWith(AndroidJUnit4::class)
class DebugApplicationTest {
  @Test
  fun incorrectContextUseViolationIsNotFatal() {
    // WebView/Chromium triggers this on configuration changes (b/296928070); it must not crash.
    assertThat(DebugApplication.isFatalViolation(mock<IncorrectContextUseViolation>())).isFalse()
  }

  @Test
  fun leakedClosableViolationIsFatal() {
    assertThat(DebugApplication.isFatalViolation(mock<LeakedClosableViolation>())).isTrue()
  }

  @Test
  fun untaggedSocketViolationIsFatal() {
    assertThat(DebugApplication.isFatalViolation(mock<UntaggedSocketViolation>())).isTrue()
  }
}
