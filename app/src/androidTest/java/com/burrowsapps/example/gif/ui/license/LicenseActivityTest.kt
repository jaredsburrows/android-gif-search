package com.burrowsapps.example.gif.ui.license

import android.Manifest.permission.INTERNET
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.burrowsapps.example.gif.R
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import test.ScreenshotWatcher
import javax.inject.Inject

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
class LicenseActivityTest {
  @get:Rule(order = 0)
  val hiltRule = HiltAndroidRule(this)

  @get:Rule(order = 1)
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  @get:Rule(order = 2)
  var composeTestRule = createAndroidComposeRule<LicenseActivity>()

  @get:Rule(order = 3)
  val permissionRule: GrantPermissionRule = GrantPermissionRule
    .grant(INTERNET, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)

  @get:Rule(order = 4)
  val screenshotWatcher = ScreenshotWatcher()

  @Inject @ApplicationContext internal lateinit var context: Context

  @Before
  fun setUp() {
    hiltRule.inject()
  }

  @Test
  fun testLicensesActivityTitleIsShowing() {
    composeTestRule.onNodeWithText(context.getString(R.string.menu_licenses))
      .assertIsDisplayed()
  }
}
