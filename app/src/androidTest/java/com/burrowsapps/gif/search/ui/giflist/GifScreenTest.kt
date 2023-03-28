package com.burrowsapps.gif.search.ui.giflist

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.MainActivity
import com.burrowsapps.gif.search.R
import com.burrowsapps.gif.search.test.TestFileUtils.MOCK_SERVER_PORT
import com.burrowsapps.gif.search.test.TestFileUtils.getMockGifResponse
import com.burrowsapps.gif.search.test.TestFileUtils.getMockResponse
import com.burrowsapps.gif.search.test.TestFileUtils.getMockWebpResponse
import com.burrowsapps.gif.search.test.onBackPressed
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import javax.inject.Inject

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
class GifScreenTest {
  @get:Rule(order = 0)
  internal val hiltRule = HiltAndroidRule(this)

  @get:Rule(order = 1)
  internal val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Inject
  @ApplicationContext
  internal lateinit var context: Context

  private val server = MockWebServer()

  @Before
  fun setUp() {
    hiltRule.inject()

    server.apply {
      dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
          request.path.orEmpty().apply {
            return when {
              contains(other = "v1/trending") -> getMockResponse(fileName = "/trending_results.json")
              contains(other = "v1/search") -> getMockResponse(fileName = "/search_results.json")
              endsWith(suffix = ".png") -> getMockWebpResponse(fileName = "/ic_launcher.webp")
              endsWith(suffix = ".gif") -> getMockGifResponse(fileName = "/android.gif")
              else -> MockResponse().setResponseCode(code = HTTP_NOT_FOUND)
            }
          }
        }
      }

      start(MOCK_SERVER_PORT)
    }
  }

  @After
  fun tearDown() {
    server.shutdown()
  }

  @Test
  fun testGifActivityTitleIsShowing() {
    composeTestRule.onNodeWithText(text = context.getString(R.string.gif_screen_title))
      .assertIsDisplayed()
  }

  @Test
  fun testLicenseMenuOpensLicenseScreen() {
    composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.menu_more))
      .performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.license_screen_title))
      .performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.license_screen_title))
      .assertIsDisplayed()
  }

  @Test
  fun testGoBackViaHardwareBackButton() {
    composeTestRule.onNodeWithText(text = context.getString(R.string.gif_screen_title))
      .assertIsDisplayed()

    composeTestRule.onBackPressed()
    composeTestRule.waitForIdle()

    // TODO: assert back
  }

  @Test
  fun testOpensLicenseScreenAndGoBack() {
    composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.menu_more))
      .performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.license_screen_title))
      .performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.license_screen_title))
      .assertIsDisplayed()

    composeTestRule.onBackPressed()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.gif_screen_title))
      .assertIsDisplayed()
  }

  @Test
  fun testTrendingThenClickOpenDialog() {
    composeTestRule.runOnUiThread {
      composeTestRule.mainClock.autoAdvance = false
    }

    composeTestRule.mainClock.advanceTimeBy(milliseconds = 2_000L)
    composeTestRule.waitForIdle()

    composeTestRule.onAllNodesWithContentDescription(label = context.getString(R.string.gif_image))
      .onFirst()
      .performClick()

    composeTestRule.runOnUiThread {
      composeTestRule.mainClock.advanceTimeBy(milliseconds = 2_000L)
    }
    composeTestRule.waitForIdle()

    composeTestRule.onAllNodes(isDialog()).assertCountEquals(1)

    composeTestRule.runOnUiThread {
      composeTestRule.mainClock.advanceTimeBy(milliseconds = 2_000L)
    }
    composeTestRule.waitForIdle()

    composeTestRule
      .onAllNodesWithContentDescription(label = context.getString(R.string.gif_image_dialog))
      .onFirst()
      .assertIsDisplayed()

    composeTestRule.onNodeWithText(text = context.getString(R.string.copy_url))
      .assertIsDisplayed()
  }

  @Test
  fun testTrendingThenClickOpenDialogAndCopyLink() {
    composeTestRule.runOnUiThread {
      composeTestRule.mainClock.autoAdvance = false
    }

    composeTestRule.mainClock.advanceTimeBy(milliseconds = 2_000L)
    composeTestRule.waitForIdle()

    composeTestRule.onAllNodesWithContentDescription(label = context.getString(R.string.gif_image))
      .onFirst()
      .performClick()

    composeTestRule.runOnUiThread {
      composeTestRule.mainClock.advanceTimeBy(milliseconds = 2_000L)
    }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.copy_url))
      .performClick()

    val clipboardManager = context.getSystemService(ClipboardManager::class.java)
    assertThat(
      clipboardManager.primaryClip?.getItemAt(0)?.coerceToText(context).toString(),
    ).matches("http.*localhost.*gif")
  }

  @Test
  fun testSearchAndGoBackViaHardwareBackButton() {
    composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.menu_search))
      .performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.search_gifs)).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.search_gifs))
      .performTextInput("hello")
    composeTestRule.waitForIdle()

    composeTestRule.onBackPressed()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.gif_screen_title))
      .assertIsDisplayed()
  }

  @Test
  fun testSearchAndClickClear() {
    composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.menu_search))
      .performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.search_gifs)).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.search_gifs))
      .performTextInput("hello")
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = "hello").assertIsDisplayed()

    composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.menu_close))
      .performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = "hello").assertDoesNotExist()
  }

  @Test
  fun testSearchAndClickBackButtonClear() {
    composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.menu_search))
      .performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.search_gifs)).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.search_gifs))
      .performTextInput("hello")
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = "hello").assertIsDisplayed()

    composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.menu_back))
      .performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = "hello").assertDoesNotExist()
  }
}
