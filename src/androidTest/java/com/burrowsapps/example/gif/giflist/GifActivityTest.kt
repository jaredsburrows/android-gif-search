package com.burrowsapps.example.gif.giflist

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.burrowsapps.example.gif.R
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import test.ScreenshotWatcher
import test.TestFileUtils.MOCK_SERVER_PORT
import test.TestFileUtils.getMockFileResponse
import test.TestFileUtils.getMockResponse
import test.launchActivity
import java.net.HttpURLConnection.HTTP_NOT_FOUND

@RunWith(AndroidJUnit4::class)
class GifActivityTest {
  @get:Rule val screenshotWatcher = ScreenshotWatcher()
  @get:Rule val grantPermissionRule = GrantPermissionRule.grant(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
  @get:Rule val activityRule = ActivityTestRule<GifActivity>(GifActivity::class.java, true, false)
  private val server = MockWebServer()
  private val mockDispatcher = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse = when {
      request.path.contains("v1/trending") -> getMockResponse("/trending_results.json")
      request.path.contains("v1/search") -> getMockResponse("/search_results.json")
      request.path.contains("images") -> getMockFileResponse("/ic_launcher.png")
      else -> MockResponse().setResponseCode(HTTP_NOT_FOUND)
    }
  }

  @Before fun setUp() {
    server.apply {
      start(MOCK_SERVER_PORT)
      dispatcher = mockDispatcher
    }
  }

  @After fun tearDown() {
    server.shutdown()
  }

  @Ignore("on view 'Animations or transitions are enabled on the target device.")
  @Test fun testTrendingThenClickOpenDialog() {
    activityRule.launchActivity()

    // Select 0, the response only contains 1 item
    onView(withId(R.id.recyclerView))
      .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
    onView(withId(R.id.gifDialogImage))
      .inRoot(isDialog())
      .check(matches(isDisplayed()))
    onView(withId(R.id.gifDialogImage))
      .inRoot(isDialog())
      .perform(pressBack())
  }

  @Test fun testTrendingResultsThenSearchThenBackToTrending() {
    activityRule.launchActivity()
    screenshotWatcher.capture("After launch")

    onView(withId(R.id.menu_search))
      .perform(click())
    screenshotWatcher.capture("After click")

    // android.support.v7.appcompat.R.id.search_src_text sometimes is not found
    onView(withHint("Search Gifs"))
      .perform(click(), typeText("hello"), closeSoftKeyboard(), pressBack())
    screenshotWatcher.capture("After Search")

    onView(withId(R.id.recyclerView))
      .check(matches(isDisplayed()))
    screenshotWatcher.capture("List displayed")
  }
}
