package test

import android.app.Activity
import android.support.test.rule.ActivityTestRule

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */

fun <T : Activity> ActivityTestRule<T>.launchActivity() {
  launchActivity(null)
}
