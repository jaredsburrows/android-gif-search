package com.burrowsapps.gif.search.ui.license

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.MainActivity
import com.burrowsapps.gif.search.R
import com.burrowsapps.gif.search.di.ApiConfigModule
import com.burrowsapps.gif.search.di.AppConfigModule
import com.burrowsapps.gif.search.test.TestFileUtils.MOCK_SERVER_PORT
import com.burrowsapps.gif.search.test.TestFileUtils.getMockGifResponse
import com.burrowsapps.gif.search.test.TestFileUtils.getMockResponse
import com.burrowsapps.gif.search.test.onBackPressed
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.SkipLeakDetection
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
class LicenseScreenTest {
  @get:Rule(order = 0)
  internal val hiltRule = HiltAndroidRule(this)

  @get:Rule(order = 1)
  internal val composeTestRule = createAndroidComposeRule<MainActivity>()

  @get:Rule(order = 2)
  internal val leakCanaryRule = DetectLeaksAfterTestSuccess()

  @Inject
  @ApplicationContext
  internal lateinit var context: Context

  private val gifScreenTitle by lazy { context.getString(R.string.gif_screen_title) }
  private val licenseScreenTitle by lazy { context.getString(R.string.license_screen_title) }
  private val licenseScreenContentDescription by lazy { context.getString(R.string.license_screen_content_description) }
  private val menuBackContentDescription by lazy { context.getString(R.string.menu_back_content_description) }
  private val menuMoreContentDescription by lazy { context.getString(R.string.menu_more_content_description) }

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

  @SkipLeakDetection("https://issuetracker.google.com/issues/296928070")
  @Test
  fun testLicenseScreenTitleIsShowing() {
    openLicenseScreen()

    composeTestRule.onNodeWithText(text = licenseScreenTitle).assertIsDisplayed()
  }

  @SkipLeakDetection("https://issuetracker.google.com/issues/296928070")
  @Test
  fun testGoBackViaHardwareBackButton() {
    openLicenseScreen()

    composeTestRule.mainClock.autoAdvance = false
    composeTestRule.onBackPressed()
    composeTestRule.mainClock.advanceTimeByFrame()
    composeTestRule.waitForIdle()
    composeTestRule.mainClock.advanceTimeByFrame()

    composeTestRule.onNodeWithText(text = gifScreenTitle).assertIsDisplayed()
  }

  @SkipLeakDetection("https://issuetracker.google.com/issues/296928070")
  @Test
  fun testGoBackViaClickMenuBackButton() {
    openLicenseScreen()

    composeTestRule.mainClock.autoAdvance = false
    composeTestRule.onNodeWithContentDescription(label = menuBackContentDescription).performClick()
    composeTestRule.mainClock.advanceTimeByFrame()
    composeTestRule.waitForIdle()
    composeTestRule.mainClock.advanceTimeByFrame()

    composeTestRule.onNodeWithText(text = gifScreenTitle).assertIsDisplayed()
  }

  private fun openLicenseScreen() {
    composeTestRule.onNodeWithContentDescription(label = menuMoreContentDescription).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.mainClock.autoAdvance = false
    composeTestRule
      .onNodeWithContentDescription(label = licenseScreenContentDescription)
      .performClick()
    composeTestRule.mainClock.advanceTimeByFrame()
    composeTestRule.waitForIdle()
    composeTestRule.mainClock.advanceTimeByFrame()

    composeTestRule.mainClock.autoAdvance = true
    composeTestRule.waitForIdle()
  }
}
