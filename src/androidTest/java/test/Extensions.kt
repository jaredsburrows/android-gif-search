package test

import android.app.Activity
import android.support.test.rule.ActivityTestRule

fun <T : Activity> ActivityTestRule<T>.launchActivity() {
  launchActivity(null)
}
