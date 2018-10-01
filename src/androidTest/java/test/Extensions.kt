package test

import android.app.Activity
import androidx.test.rule.ActivityTestRule

fun <T : Activity> ActivityTestRule<T>.launchActivity() {
    launchActivity(null)
}
