package com.burrowsapps.example.gif.ui.giflist

import android.app.Activity
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.example.gif.MainActivity
import com.burrowsapps.example.gif.R
import com.burrowsapps.example.gif.test.TestFileUtils.MOCK_SERVER_PORT
import com.burrowsapps.example.gif.test.TestFileUtils.getMockFileResponse
import com.burrowsapps.example.gif.test.TestFileUtils.getMockResponse
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
  internal var composeTestRule = createAndroidComposeRule<MainActivity>()

  @Inject @ApplicationContext internal lateinit var context: Context

  private val server = MockWebServer()

  @Before
  fun setUp() {
    hiltRule.inject()

    init()

    server.apply {
      dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
          request.path.orEmpty().apply {
            return when {
              contains(other = "v1/trending") -> getMockResponse(fileName = "/trending_results.json")
              contains(other = "v1/search") -> getMockResponse(fileName = "/search_results.json")
              endsWith(suffix = ".png") -> getMockFileResponse(fileName = "/ic_launcher.webp")
              endsWith(suffix = ".gif") -> getMockFileResponse(fileName = "/ic_launcher.webp")
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

    release()
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

    pressBackUnconditionally()
    composeTestRule.waitForIdle()

    assertThat(composeTestRule.activityRule.scenario.result.resultCode)
      .isEqualTo(Activity.RESULT_CANCELED)
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

    pressBackUnconditionally()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = context.getString(R.string.gif_screen_title))
      .assertIsDisplayed()
  }

  @Test
  fun testTrendingThenClickOpenDialog() {
    composeTestRule.onNodeWithContentDescription(label = "Gif List").performClick()
    composeTestRule.waitForIdle()

//    composeTestRule.onNodeWithContentDescription(label = context.getString(R.string.gif_image)).performClick()
    composeTestRule.waitForIdle()

    // TODO assert dialog
//    composeTestRule.onNode(isDialog()).assertIsDisplayed()
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

    pressBackUnconditionally()
    pressBackUnconditionally()
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
