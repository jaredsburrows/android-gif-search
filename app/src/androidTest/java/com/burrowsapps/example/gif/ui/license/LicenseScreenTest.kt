package com.burrowsapps.example.gif.ui.license

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.example.gif.MainActivity
import com.burrowsapps.example.gif.R
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import javax.inject.Inject

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
class LicenseScreenTest {
  @get:Rule(order = 0)
  internal val hiltRule = HiltAndroidRule(this)

  @get:Rule(order = 1)
  internal var composeTestRule = createAndroidComposeRule<MainActivity>()

  @Inject @ApplicationContext internal lateinit var context: Context

  @Before
  fun setUp() {
    hiltRule.inject()

    init()

    composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.menu_more))
      .performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.license_screen_title))
      .performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.license_screen_title))
      .assertIsDisplayed()
  }

  @After
  fun tearDown() {
    release()
  }

  @Test
  fun testLicenseScreenTitleIsShowing() {
    composeTestRule.onNodeWithText(text = context.getString(R.string.license_screen_title))
      .assertIsDisplayed()
  }

  @Test
  fun testGoBackViaHardwareBackButton() {
    composeTestRule.onNodeWithText(text = context.getString(R.string.license_screen_title))
      .assertIsDisplayed()

    pressBackUnconditionally()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.gif_screen_title))
      .assertIsDisplayed()
  }

  @Test
  fun testGoBackViaBackButton() {
    composeTestRule.onNodeWithText(text = context.getString(R.string.license_screen_title))
      .assertIsDisplayed()

    composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.menu_back))
      .performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.gif_screen_title))
      .assertIsDisplayed()
  }
}
