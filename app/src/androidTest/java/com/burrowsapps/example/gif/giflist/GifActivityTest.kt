package com.burrowsapps.example.gif.giflist

import android.Manifest.permission.INTERNET
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import test.ScreenshotWatcher
import test.TestFileUtils
import test.TestFileUtils.getMockFileResponse
import test.TestFileUtils.getMockResponse
import java.net.HttpURLConnection.HTTP_NOT_FOUND

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GifActivityTest {
  @get:Rule(order = 0)
  val hiltRule = HiltAndroidRule(this)

  @get:Rule(order = 1)
  val activityScenarioRule = ActivityScenarioRule(GifActivity::class.java)

  @get:Rule(order = 2)
  val permissionRule: GrantPermissionRule = GrantPermissionRule
    .grant(INTERNET, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)

  @get:Rule(order = 3)
  val screenshotWatcher = ScreenshotWatcher()

  private val server = MockWebServer()

  @Before fun setUp() {
    hiltRule.inject()

    server.apply {
      dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
          request.path.orEmpty().apply {
            return when {
              contains("v1/trending") -> getMockResponse("/trending_results.json")
              contains("v1/search") -> getMockResponse("/search_results.json")
              contains("images") -> getMockFileResponse("/ic_launcher.png")
              else -> MockResponse().setResponseCode(HTTP_NOT_FOUND)
            }
          }
        }
      }

      start(TestFileUtils.MOCK_SERVER_PORT)
    }
  }

  @After fun tearDown() {
    server.shutdown()
  }

  @Test fun testTrendingThenClickOpenDialog() {
    screenshotWatcher.capture("After launch")

    // Select 0, the response only contains 1 item
    onView(withId(com.burrowsapps.example.gif.R.id.recyclerView))
      .perform(
        actionOnItem<RecyclerView.ViewHolder>(
          hasDescendant(withId(com.burrowsapps.example.gif.R.id.gifImage)),
          click()
        ).atPosition(0)
      )
    screenshotWatcher.capture("After click")

    onView(withId(com.burrowsapps.example.gif.R.id.gifDialogTitle))
      .perform(pressBack())
  }

  @Test fun testTrendingResultsThenSearchThenBackToTrending() {
    screenshotWatcher.capture("After launch")

    onView(withId(com.burrowsapps.example.gif.R.id.menuSearch))
      .perform(click())
    screenshotWatcher.capture("After click")

    onView(withId(androidx.appcompat.R.id.search_src_text))
      .perform(click(), typeText("hello"), closeSoftKeyboard(), pressBack())
    screenshotWatcher.capture("After Search")

    onView(withId(com.burrowsapps.example.gif.R.id.recyclerView))
      .perform(pressBack())
    screenshotWatcher.capture("List displayed")
  }
}
