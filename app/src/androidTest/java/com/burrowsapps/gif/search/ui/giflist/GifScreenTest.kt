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
import com.burrowsapps.gif.search.di.ApiConfigModule
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
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(ApiConfigModule::class)
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

  private val gifScreenTitle by lazy { context.getString(R.string.gif_screen_title) }
  private val licenseScreenTitle by lazy { context.getString(R.string.license_screen_title) }
  private val searchGifs by lazy { context.getString(R.string.search_gifs) }
  private val menuMore by lazy { context.getString(R.string.menu_more) }
  private val menuSearch by lazy { context.getString(R.string.menu_search) }
  private val menuClose by lazy { context.getString(R.string.menu_close) }
  private val menuBack by lazy { context.getString(R.string.menu_back) }
  private val copyUrl by lazy { context.getString(R.string.copy_url) }
  private val gifImage by lazy { context.getString(R.string.gif_image) }
  private val gifImageDialog by lazy { context.getString(R.string.gif_image_dialog) }

  @Before
  fun setUp() {
    hiltRule.inject()
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
                request.path.orEmpty().apply {
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
    composeTestRule.onNodeWithText(text = gifScreenTitle).assertIsDisplayed()
  }

  @Test
  fun testLicenseMenuOpensLicenseScreen() {
    openLicenseScreen()

    composeTestRule.onNodeWithText(text = licenseScreenTitle).assertIsDisplayed()
  }

  @Test
  fun testOpensLicenseScreenAndGoBack() {
    openLicenseScreen()

    composeTestRule.onNodeWithText(text = licenseScreenTitle).assertIsDisplayed()
    composeTestRule.waitForIdle()

    composeTestRule.onBackPressed()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = gifScreenTitle).assertIsDisplayed()
  }

  @Test
  fun testTrendingThenClickOpenDialog() {
    composeTestRule.mainClock.autoAdvance = false
    composeTestRule.mainClock.advanceTimeBy(milliseconds = 2_000L)

    composeTestRule.onAllNodesWithContentDescription(label = gifImage).onFirst().performClick()
    composeTestRule.mainClock.advanceTimeBy(milliseconds = 2_000L)

    composeTestRule.onAllNodes(isDialog()).assertCountEquals(1)
    composeTestRule.onNode(isDialog()).assertIsDisplayed()
    composeTestRule.mainClock.advanceTimeBy(milliseconds = 2_000L)

    composeTestRule.onAllNodesWithContentDescription(label = gifImageDialog).onFirst()
      .assertIsDisplayed()
    composeTestRule.onNodeWithText(text = copyUrl).assertIsDisplayed()
  }

  @Ignore("dialog causes flakiness")
  @Test
  fun testTrendingThenClickOpenDialogAndCopyLink() {
    composeTestRule.mainClock.autoAdvance = false
    composeTestRule.mainClock.advanceTimeBy(milliseconds = 2_000L)

    composeTestRule.onAllNodesWithContentDescription(label = gifImage).onFirst().performClick()
    composeTestRule.mainClock.advanceTimeBy(milliseconds = 2_000L)

    composeTestRule.onNodeWithText(text = copyUrl).performClick()
    composeTestRule.mainClock.advanceTimeBy(milliseconds = 2_000L)

    val clipboardManager = context.getSystemService(ClipboardManager::class.java)
    assertThat(
      clipboardManager.primaryClip?.getItemAt(0)?.coerceToText(context).toString(),
    ).matches("http.*localhost.*gif")
  }

  @Ignore("flakiness")
  @Test
  fun testSearchAndCancelViaHardwareBackButton() {
    enterSearchMode()

    performSearchInput("hello")

    composeTestRule.onBackPressed()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = gifScreenTitle).assertIsDisplayed()
  }

  @Ignore("flakiness")
  @Test
  fun testSearchAndCancelByClickingClear() {
    enterSearchMode()

    performSearchInput("hello")

    composeTestRule.onNodeWithText(text = "hello").assertIsDisplayed()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithContentDescription(label = menuClose).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = "hello").assertDoesNotExist()
  }

  @Ignore("flakiness")
  @Test
  fun testSearchAndClickMenuBackButtonClear() {
    enterSearchMode()

    performSearchInput("hello")

    composeTestRule.onNodeWithText(text = "hello").assertIsDisplayed()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithContentDescription(label = menuBack).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = "hello").assertDoesNotExist()
  }

  private fun openLicenseScreen() {
    composeTestRule.onNodeWithContentDescription(label = menuMore).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = licenseScreenTitle).performClick()
    composeTestRule.waitForIdle()
  }

  private fun enterSearchMode() {
    composeTestRule.onNodeWithContentDescription(label = menuSearch).performClick()
    composeTestRule.waitForIdle()
  }

  private fun performSearchInput(
    @Suppress("SameParameterValue") searchText: String,
  ) {
    composeTestRule.onNodeWithText(text = searchGifs).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = searchGifs).performTextInput(searchText)
    composeTestRule.waitForIdle()
  }
}
