package com.burrowsapps.gif.search.test

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule

/**
 * Simulates pressing the back button on the activity associated with this test rule.
 *
 * This function is an extension function for the AndroidComposeTestRule class, and can be called
 * directly on an instance of that class. It calls the onBackPressed() method of the activity's
 * onBackPressedDispatcher, which is equivalent to pressing the back button on the device or
 * emulator.
 *
 * @throws IllegalStateException if the activity is not currently in the resumed state.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onBackPressed() {
  this.activityRule.scenario.onActivity { activity ->
    activity.onBackPressedDispatcher.onBackPressed()
  }
}
