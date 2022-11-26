package com.burrowsapps.gif.search.test

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class DisableAnimationsRule : TestRule {
  override fun apply(base: Statement, description: Description): Statement {
    return object : Statement() {
      override fun evaluate() {
        // disable animations for test run
        changeAnimationStatus(enable = false)
        try {
          base.evaluate()
        } finally {
          // enable after test run
          changeAnimationStatus(enable = true)
        }
      }

      private fun changeAnimationStatus(enable: Boolean = true) {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).apply {
          executeShellCommand("settings put global window_animation_scale ${enable.toInt()}")
          executeShellCommand("settings put global transition_animation_scale ${enable.toInt()}")
          executeShellCommand("settings put global animator_duration_scale ${enable.toInt()}")
        }
      }

      private fun Boolean.toInt() = if (this) 1 else 0
    }
  }
}
