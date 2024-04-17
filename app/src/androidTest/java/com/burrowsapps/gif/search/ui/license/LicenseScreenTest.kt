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
import com.burrowsapps.gif.search.test.TestFileUtils.MOCK_SERVER_PORT
import com.burrowsapps.gif.search.test.TestFileUtils.getMockGifResponse
import com.burrowsapps.gif.search.test.TestFileUtils.getMockResponse
import com.burrowsapps.gif.search.test.onBackPressed
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
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
@UninstallModules(ApiConfigModule::class)
@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
class LicenseScreenTest {
  @get:Rule(order = 0)
  internal val hiltRule = HiltAndroidRule(this)

  @get:Rule(order = 1)
  internal val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Inject
  @ApplicationContext
  internal lateinit var context: Context

  private val server = MockWebServer()
  private val menuMore by lazy { context.getString(R.string.menu_more) }
  private val licenseScreenTitle by lazy { context.getString(R.string.license_screen_title) }
  private val gifScreenTitle by lazy { context.getString(R.string.gif_screen_title) }
  private val menuBack by lazy { context.getString(R.string.menu_back) }

  @Before
  fun setUp() {
    hiltRule.inject()

    server.apply {
      dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
          request.path.orEmpty().apply {
            return when {
              // Matches URL pattern for trending on Tenor with parameters
              matches(Regex("^/v1/trending.*")) -> getMockResponse(fileName = "/trending_results.json")

              // Matches URL pattern for search on Tenor with parameters
              matches(Regex("^/v1/search.*")) -> getMockResponse(fileName = "/search_results.json")

              // Handling image files with specific response
              matches(Regex(".*/[^/]+\\.(png|gif)$")) -> getMockGifResponse(fileName = "/ic_launcher.webp")

              else -> MockResponse().setResponseCode(code = HTTP_NOT_FOUND)
            }
          }
        }
      }

      start(MOCK_SERVER_PORT)
    }

    openLicenseScreen()
  }

  @After
  fun tearDown() {
    server.shutdown()
  }

  @Test
  fun testLicenseScreenTitleIsShowing() {
    composeTestRule.onNodeWithText(text = licenseScreenTitle).assertIsDisplayed()
  }

  @Test
  fun testGoBackViaHardwareBackButton() {
    composeTestRule.onBackPressed()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = gifScreenTitle).assertIsDisplayed()
  }

  @Test
  fun testGoBackViaClickMenuBackButton() {
    composeTestRule.onNodeWithContentDescription(label = menuBack).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = gifScreenTitle).assertIsDisplayed()
  }

  private fun openLicenseScreen() {
    composeTestRule.onNodeWithContentDescription(label = menuMore).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText(text = licenseScreenTitle).performClick()
    composeTestRule.waitForIdle()
  }
}
