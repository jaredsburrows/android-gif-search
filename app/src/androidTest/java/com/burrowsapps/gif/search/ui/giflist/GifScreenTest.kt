package com.burrowsapps.gif.search.ui.giflist

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
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
import com.burrowsapps.gif.search.di.ApiConfigModule
import com.burrowsapps.gif.search.di.AppConfigModule
import com.burrowsapps.gif.search.test.TestFileUtils.MOCK_SERVER_PORT
import com.burrowsapps.gif.search.test.TestFileUtils.getMockGifResponse
import com.burrowsapps.gif.search.test.TestFileUtils.getMockResponse
import com.burrowsapps.gif.search.test.onBackPressed
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import leakcanary.DetectLeaksAfterTestSuccess
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import timber.log.Timber
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(ApiConfigModule::class, AppConfigModule::class)
@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
class GifScreenTest {
  @get:Rule(order = 0)
  internal val hiltRule = HiltAndroidRule(this)

  @get:Rule(order = 1)
  internal val composeTestRule = createAndroidComposeRule<MainActivity>()

  @get:Rule(order = 2)
  internal val leakCanaryRule = DetectLeaksAfterTestSuccess()

  @Inject
  @ApplicationContext
  internal lateinit var context: Context

  // Title text not asserted in current UI; search icon is used instead
  private val copyUrl by lazy { context.getString(R.string.copy_url) }
  private val menuCloseContentDescription by lazy { context.getString(R.string.menu_close_content_description) }
  private val menuSearchContentDescription by lazy { context.getString(R.string.menu_search_content_description) }
  private val gifImageContentDescription by lazy { context.getString(R.string.gif_image_content_description) }
  private val gifImageDialogContentDescription by lazy { context.getString(R.string.gif_image_dialog_content_description) }

  @Before
  fun setUp() {
    hiltRule.inject()

    // Disable auto-advance so that Compose does not wait indefinitely for idle.
    // Landscapist 2.9.x introduces produceState coroutines (rememberImageSourceFile) and
    // explicit Loading state emissions that, combined with animated GIF drawables, prevent
    // Compose from ever reaching a fully idle state within the test timeout.
    composeTestRule.mainClock.autoAdvance = false
    composeTestRule.mainClock.advanceTimeByFrame()
    composeTestRule.waitForIdle()
  }

  companion object {
    private lateinit var webServer: MockWebServer

    @BeforeClass
    @JvmStatic
    fun startMockServer() {
      webServer =
        MockWebServer().apply {
          dispatcher =
            object : Dispatcher() {
              override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path.orEmpty()
                Timber.i("Request path: $path")

                path.apply {
                  return when {
                    // Matches URL pattern for trending on Tenor with parameters
                    matches(Regex("^/v1/trending.*")) -> getMockResponse(fileName = "/trending_results.json")

                    // Matches URL pattern for search on Tenor with parameters
                    matches(Regex("^/v1/search.*")) -> getMockResponse(fileName = "/search_results.json")

                    // Handling image files with specific response
                    matches(Regex(".*/[^/]+\\.(png|gif)$")) -> getMockGifResponse(fileName = "/android.gif")

                    else -> MockResponse().setResponseCode(code = HTTP_NOT_FOUND)
                  }
                }
              }
            }

          start(MOCK_SERVER_PORT)
        }
    }

    @AfterClass
    @JvmStatic
    fun shutDownServer() {
      webServer.shutdown()
    }
  }

  @Test
  fun testGifActivityTitleIsShowing() {
    // With TopSearchBar, validate the search icon is visible as the primary affordance
    composeTestRule.mainClock.advanceTimeByFrame()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithContentDescription(label = menuSearchContentDescription).assertIsDisplayed()
  }

  @Test
  fun testTrendingThenClickOpenDialog() {
    // Wait until the gifs are showing
    composeTestRule.waitUntil(
      condition = {
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule
          .onAllNodesWithContentDescription(label = gifImageContentDescription)
          .fetchSemanticsNodes()
          .isNotEmpty()
      },
      timeoutMillis = 10_000,
    )

    // Perform click on the first node with the content description
    composeTestRule
      .onAllNodesWithContentDescription(label = gifImageContentDescription)
      .onFirst()
      .performClick()
    composeTestRule.mainClock.advanceTimeByFrame()
    composeTestRule.waitForIdle()
    composeTestRule.mainClock.advanceTimeByFrame()

    // Assert that the dialog is displayed (uses real Dialog composable)
    composeTestRule.onNode(isDialog()).assertIsDisplayed()
    composeTestRule.waitForIdle()

    // Assert that the gif image content description is visible in the dialog
    composeTestRule
      .onNodeWithContentDescription(label = gifImageDialogContentDescription)
      .assertIsDisplayed()
  }

  @Test
  fun testTrendingThenClickOpenDialogAndCopyLink() {
    // Wait until the gifs are showing
    composeTestRule.waitUntil(
      condition = {
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule
          .onAllNodesWithContentDescription(label = gifImageContentDescription)
          .fetchSemanticsNodes()
          .isNotEmpty()
      },
      timeoutMillis = 10_000,
    )

    // Perform click on the first node with the content description
    composeTestRule
      .onAllNodesWithContentDescription(label = gifImageContentDescription)
      .onFirst()
      .performClick()
    composeTestRule.mainClock.advanceTimeByFrame()
    composeTestRule.waitForIdle()
    composeTestRule.mainClock.advanceTimeByFrame()

    // Click copy URL in the dialog
    composeTestRule.onNodeWithText(text = copyUrl).performClick()
    composeTestRule.mainClock.advanceTimeByFrame()
    composeTestRule.waitForIdle()

    // Assert that the clipboard has the correct URL
    val clipboardManager = context.getSystemService(ClipboardManager::class.java)
    assertThat(
      clipboardManager.primaryClip
        ?.getItemAt(0)
        ?.coerceToText(context)
        .toString(),
    ).matches("http.*localhost.*gif")
  }

  @Test
  fun testSearchAndCancelViaHardwareBackButton() {
    enterSearchMode()

    performSearchInput("hello")

    composeTestRule.onNodeWithText(text = "hello").assertIsDisplayed()
    composeTestRule.mainClock.advanceTimeByFrame()
    composeTestRule.waitForIdle()

    composeTestRule.onBackPressed()
    composeTestRule.mainClock.advanceTimeByFrame()
    composeTestRule.waitForIdle()
    composeTestRule.mainClock.advanceTimeByFrame()

    composeTestRule.onNodeWithContentDescription(label = menuSearchContentDescription).assertIsDisplayed()
  }

  @Test
  fun testSearchAndCancelByClickingClear() {
    enterSearchMode()

    performSearchInput("hello")

    composeTestRule.onNodeWithText(text = "hello").assertIsDisplayed()
    composeTestRule.mainClock.advanceTimeByFrame()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithContentDescription(label = menuCloseContentDescription).performClick()
    composeTestRule.mainClock.advanceTimeByFrame()
    composeTestRule.waitForIdle()
    composeTestRule.mainClock.advanceTimeByFrame()

    composeTestRule.onNodeWithText(text = "hello").assertDoesNotExist()
  }

  private fun enterSearchMode() {
    composeTestRule
      .onNodeWithContentDescription(label = menuSearchContentDescription)
      .performClick()
    composeTestRule.mainClock.advanceTimeByFrame()
    composeTestRule.waitForIdle()
  }

  private fun performSearchInput(
    @Suppress("SameParameterValue") searchText: String,
  ) {
    // Focus the editable search field directly
    composeTestRule.onNode(hasSetTextAction()).performClick()
    composeTestRule.mainClock.advanceTimeByFrame()
    composeTestRule.waitForIdle()

    composeTestRule.onNode(hasSetTextAction()).performTextInput(searchText)
    composeTestRule.mainClock.advanceTimeByFrame()
    composeTestRule.waitForIdle()
  }
}
