package test

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
import android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
import burrows.apps.example.gif.TestApp

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
open class CustomTestRule<T : Activity> : ActivityTestRule<T> {
  constructor(activityClass: Class<T>) : super(activityClass)
  constructor(activityClass: Class<T>, initialTouchMode: Boolean) : super(activityClass, initialTouchMode)
  constructor(activityClass: Class<T>, initialTouchMode: Boolean, launchActivity: Boolean) : super(activityClass, initialTouchMode, launchActivity)

  val application: TestApp
    get() = targetContext.applicationContext as TestApp

  val targetContext: Context
    get() = instrumentation.targetContext

  val instrumentation: Instrumentation
    get() = InstrumentationRegistry.getInstrumentation()

  fun launchActivity() {
    launchActivity(Intent(ACTION_MAIN))
  }

  // Lambda causes test to fail here, expanded for now
  fun keepScreenOn() {
    val activity = activity
    val wakeUpDevice = Runnable { activity.window.addFlags(FLAG_TURN_SCREEN_ON or FLAG_SHOW_WHEN_LOCKED or FLAG_KEEP_SCREEN_ON) }
    activity.runOnUiThread(wakeUpDevice)
  }
}
