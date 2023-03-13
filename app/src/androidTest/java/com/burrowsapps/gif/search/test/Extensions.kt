package com.burrowsapps.gif.search.test

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onBackPressed() {
  this.activityRule.scenario.onActivity { activity ->
    activity.onBackPressedDispatcher.onBackPressed()
  }
}
