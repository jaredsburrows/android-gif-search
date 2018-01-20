package burrows.apps.example.gif.presentation.main

import android.support.test.InstrumentationRegistry
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
import burrows.apps.example.gif.App
import burrows.apps.example.gif.R
import burrows.apps.example.gif.presentation.di.component.DaggerActivityComponent
import burrows.apps.example.gif.presentation.di.component.DaggerAppComponent
import burrows.apps.example.gif.presentation.di.module.AppModule
import burrows.apps.example.gif.presentation.di.module.RiffsyModule
import test.launchActivity
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
import test.TestBase
import java.net.HttpURLConnection.HTTP_NOT_FOUND

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest : TestBase() {
  companion object {
    private const val MOCK_WEB_SERVER_BASE_URL = "http://localhost:8080"
  }
  private val server = MockWebServer()
  @get:Rule val activityRule = ActivityTestRule<MainActivity>(MainActivity::class.java, true, false)

  @Before override fun setUp() {
    super.setUp()
    server.start(MOCK_SERVER_PORT)
    server.setDispatcher(dispatcher)

    val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as App
    val daggerActivityComponent = DaggerActivityComponent.builder()
      .appComponent(DaggerAppComponent.builder()
        .appModule(AppModule(app))
        .build())
      .riffsyModule(RiffsyModule(MOCK_WEB_SERVER_BASE_URL))
      .build()
    app.activityComponent = daggerActivityComponent
  }

  @After override fun tearDown() {
    super.tearDown()
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

  private val dispatcher = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse = when {
      request.path.contains("v1/trending") -> getMockResponse("/trending_results.json")
      request.path.contains("v1/search") -> getMockResponse("/search_results.json")
      request.path.contains("images") -> getMockFileResponse("/ic_launcher.png")
      else -> MockResponse().setResponseCode(HTTP_NOT_FOUND)
    }
  }
}
