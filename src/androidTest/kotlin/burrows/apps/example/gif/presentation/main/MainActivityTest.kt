package burrows.apps.example.gif.presentation.main

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions.pressBack
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.matcher.RootMatchers.isDialog
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.SmallTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import burrows.apps.example.gif.R
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import test.AndroidTestBase
import java.net.HttpURLConnection.HTTP_NOT_FOUND

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest : AndroidTestBase() {
  companion object {
    private val server = MockWebServer()

    @BeforeClass @JvmStatic fun setUpClass() {
      server.start(MOCK_SERVER_PORT)
      server.setDispatcher(dispatcher)
    }

    @AfterClass @JvmStatic fun tearDownClass() {
      server.shutdown()
    }

    private val dispatcher = object : Dispatcher() {
      override fun dispatch(request: RecordedRequest): MockResponse {
        when {
          request.path.contains("v1/trending") -> return getMockResponse("/trending_results.json")
          request.path.contains("v1/search") -> return getMockResponse("/search_results.json")
          request.path.contains("images") -> return getMockFileResponse("/ic_launcher.png")
          else -> return MockResponse().setResponseCode(HTTP_NOT_FOUND)
        }
      }
    }
  }

  @get:Rule val activityRule = ActivityTestRule<MainActivity>(MainActivity::class.java, true, false)

  @Ignore
  @Test fun testTrendingThenClickOpenDialog() {
    // Act
    activityRule.launchActivity(null)

    // Assert
    onView(withId(R.id.recyclerView))
      .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())) // Select 0, the response only contains 1 item
    onView(withId(R.id.gifDialogImage))
      .inRoot(isDialog())
      .check(matches(isDisplayed()))
    onView(withId(R.id.gifDialogImage))
      .inRoot(isDialog())
      .perform(pressBack())
  }

  @Test fun testTrendingResultsThenSearchThenBackToTrending() {
    // Act
    activityRule.launchActivity(null)

    // Assert
    onView(withId(R.id.menu_search))
      .perform(click())
    onView(withId(android.support.v7.appcompat.R.id.search_src_text))
      .perform(typeText("hello"), closeSoftKeyboard(), pressBack())
    onView(withId(R.id.recyclerView))
      .check(matches(isDisplayed()))
  }
}
