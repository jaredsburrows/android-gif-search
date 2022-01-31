package com.burrowsapps.example.gif.ui.license

import android.Manifest.permission.INTERNET
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import test.ScreenshotWatcher

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
class LicenseActivityTest {
  @get:Rule(order = 0)
  val hiltRule = HiltAndroidRule(this)

  @get:Rule(order = 1)
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  @get:Rule(order = 2)
  val activityScenarioRule: ActivityScenarioRule<LicenseActivity> =
    ActivityScenarioRule(LicenseActivity::class.java)

  @get:Rule(order = 3)
  val permissionRule: GrantPermissionRule = GrantPermissionRule
    .grant(INTERNET, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)

  @get:Rule(order = 4)
  val screenshotWatcher = ScreenshotWatcher()

  @Before
  fun setUp() {
    hiltRule.inject()
  }

  @Test
  fun testLicensesTitleIsShowing() {
    onView(
      allOf(
        instanceOf(TextView::class.java),
        withParent(instanceOf(Toolbar::class.java))
      )
    ).check(matches(withText(containsString("Open source licenses"))))
  }
}
