package burrows.apps.example.gif.giflist

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions.pressBack
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.matcher.RootMatchers.isDialog
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withHint
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
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import test.TestUtils.MOCK_SERVER_PORT
import test.TestUtils.getMockFileResponse
import test.TestUtils.getMockResponse
import test.launchActivity
import java.net.HttpURLConnection.HTTP_NOT_FOUND

@SmallTest
@RunWith(AndroidJUnit4::class)
class GifActivityTest {
  @get:Rule val activityRule = ActivityTestRule<GifActivity>(GifActivity::class.java, true, false)
  private val server = MockWebServer()
  private val dispatcher = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse = when {
      request.path.contains("v1/trending") -> getMockResponse("/trending_results.json")
      request.path.contains("v1/search") -> getMockResponse("/search_results.json")
      request.path.contains("images") -> getMockFileResponse("/ic_launcher.png")
      else -> MockResponse().setResponseCode(HTTP_NOT_FOUND)
    }
  }

  @Before fun setUp() {
    server.start(MOCK_SERVER_PORT)
    server.setDispatcher(dispatcher)
  }

  @After fun tearDown() {
    server.shutdown()
  }

  @Ignore // TODO on view 'Animations or transitions are enabled on the target device. For more info check: http://goo.gl/qVu1yV
  @Test fun testTrendingThenClickOpenDialog() {
    // Act
    activityRule.launchActivity()

    // Assert
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
    // Act
    activityRule.launchActivity()

    // Assert
    onView(withId(R.id.menu_search))
      .perform(click())
    // android.support.v7.appcompat.R.id.search_src_text sometimes is not found
    onView(withHint("Search Gifs"))
      .perform(click(), typeText("hello"), closeSoftKeyboard(), pressBack())
    onView(withId(R.id.recyclerView))
      .check(matches(isDisplayed()))
  }
}
