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
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.SmallTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import burrows.apps.example.gif.App
import burrows.apps.example.gif.R
import burrows.apps.example.gif.data.rest.repository.RiffsyApiClient
import burrows.apps.example.gif.presentation.di.component.DaggerActivityComponent
import burrows.apps.example.gif.presentation.di.component.DaggerAppComponent
import burrows.apps.example.gif.presentation.di.module.AppModule
import burrows.apps.example.gif.presentation.di.module.RiffsyModule
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer
import okio.Okio
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import test.AndroidTestBase
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import java.net.HttpURLConnection.HTTP_OK

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest : AndroidTestBase() {
  @get:Rule val activityRule: ActivityTestRule<MainActivity> = object : ActivityTestRule<MainActivity>(MainActivity::class.java, true, false) {
    override fun beforeActivityLaunched() {
      super.beforeActivityLaunched()

      // Override app component
      val testApp = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as App
      val appComponent = DaggerAppComponent.builder()
        .appModule(AppModule(testApp))
        .build()
      testApp.appComponent = appComponent

      // Override activity component
      val activityComponent = DaggerActivityComponent.builder()
        .appComponent(appComponent)
        .riffsyModule(object : RiffsyModule() {
          // Set custom endpoint for rest service
          override fun providesRiffsyApi(retrofit: Retrofit.Builder): RiffsyApiClient {
            return retrofit
              .baseUrl(mockEndPoint)
              .build()
              .create(RiffsyApiClient::class.java)
          }
        })
        .build()
      testApp.activityComponent = activityComponent
    }
  }
  @get:Rule val server = MockWebServer()
  private lateinit var mockEndPoint: String

  @Before override fun setUp() {
    super.setUp()

    mockEndPoint = server.url("/").toString()
    server.setDispatcher(dispatcher)
  }

  @After override fun tearDown() {
    super.tearDown()

    server.shutdown()
  }

  @Ignore
  @Test fun testTrendingThenClickOpenDialog() {
    // Act
    activityRule.launchActivity(null)

    // Assert
    onView(withId(R.id.recycler_view))
      .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())) // Select 0, the response only contains 1 item
    onView(withId(R.id.gif_dialog_image))
      .inRoot(isDialog())
      .check(matches(isDisplayed()))
    onView(withId(R.id.gif_dialog_image))
      .inRoot(isDialog())
      .perform(pressBack())
  }

  @Test fun testTrendingResultsThenSearchThenBackToTrending() {
    // Act
    activityRule.launchActivity(null)

    // Assert
    onView(withId(R.id.menu_search))
      .perform(click())
    onView(withId(R.id.search_src_text))
      .perform(typeText("hello"), closeSoftKeyboard(), pressBack())
    onView(withId(R.id.recycler_view))
      .check(matches(isDisplayed()))
  }

  private fun getMockResponse(fileName: String): MockResponse {
    return MockResponse()
      .setStatus("HTTP/1.1 200")
      .setResponseCode(HTTP_OK)
      .setBody(InputStreamReader(javaClass.getResourceAsStream(fileName)).readText())
      .addHeader("content-type: text/plain; charset=utf-8")
  }

  private fun getMockFileResponse(fileName: String): MockResponse {
    return MockResponse()
      .setStatus("HTTP/1.1 200")
      .setResponseCode(HTTP_OK)
      .setBody(fileToBytes(javaClass.getResourceAsStream(fileName)))
      .addHeader("content-type: image/png")
  }

  private fun fileToBytes(file: InputStream): Buffer {
    val result = Buffer()
    result.writeAll(Okio.source(file))
    return result
  }

  private val dispatcher = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
      when {
        request.path.contains("/v1/trending") -> return getMockResponse("/trending_results.json")
        request.path.contains("/v1/search") -> return getMockResponse("/search_results.json")
        request.path.contains("/images") -> return getMockFileResponse("/ic_launcher.png")
        else -> return MockResponse().setResponseCode(HTTP_NOT_FOUND)
      }
    }
  }
}
