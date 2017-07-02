package burrows.apps.example.gif.presentation.main

import android.content.Context
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
import burrows.apps.example.gif.data.rest.repository.ImageApiRepository
import burrows.apps.example.gif.data.rest.repository.RiffsyApiClient
import burrows.apps.example.gif.data.rest.repository.RiffsyApiClient.Companion.API_KEY
import burrows.apps.example.gif.presentation.di.component.DaggerActivityComponent
import burrows.apps.example.gif.presentation.di.component.DaggerAppComponent
import burrows.apps.example.gif.presentation.di.module.AppModule
import burrows.apps.example.gif.presentation.di.module.GlideModule
import burrows.apps.example.gif.presentation.di.module.RiffsyModule
import com.bumptech.glide.GifRequestBuilder
import com.bumptech.glide.Glide
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
import org.mockito.MockitoAnnotations.initMocks
import retrofit2.Retrofit
import test.AndroidTestBase
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import java.net.HttpURLConnection.HTTP_OK
import java.nio.charset.Charset
import java.util.Scanner

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@Ignore
@Suppress("UNCHECKED_CAST")
@SmallTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest : AndroidTestBase() {
  @Rule @JvmField val server = MockWebServer()
  @Rule @JvmField val activityRule: ActivityTestRule<MainActivity> = object : ActivityTestRule<MainActivity>(MainActivity::class.java) {
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
          override fun providesRiffsyApi(retrofit: Retrofit): RiffsyApiClient {
            return retrofit.newBuilder()
              .baseUrl(mockEndPoint)
              .build()
              .create(RiffsyApiClient::class.java)
          }
        })
        .glideModule(object : GlideModule() {
          override fun providesImageDownloader(context: Context): ImageApiRepository {
            return object : ImageApiRepository(context) {
              // Prevent Glide network call with custom override
              override fun <T> load(url: T): GifRequestBuilder<T> {
                return Glide.with(context)
                  .load(R.mipmap.ic_launcher)
                  .asGif()
                  .placeholder(R.mipmap.ic_launcher)
                  .fallback(R.mipmap.ic_launcher)
                  .error(R.mipmap.ic_launcher)
                  .override(200, 200) as GifRequestBuilder<T>
              }
            }
          }
        })
        .build()
      testApp.activityComponent = activityComponent
    }
  }
  private var mockEndPoint: String? = null

  @Before override fun setUp() {
    super.setUp()

    initMocks(this)

    mockEndPoint = server.url("/").toString()
    server.setDispatcher(dispatcher)
  }

  @After override fun tearDown() {
    super.tearDown()

    server.shutdown()
  }

  private fun getMockResponse(fileName: String): MockResponse {
    val stream = javaClass.getResourceAsStream(fileName)
    val mockResponse = Scanner(stream, Charset.defaultCharset().name())
      .useDelimiter("\\A").next()
    val response = MockResponse().setResponseCode(HTTP_OK)
      .setBody(mockResponse)
    stream.close()
    return response
  }

  @Ignore
  @Test fun testTrendingThenClickOpenDialog() {
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
    onView(withId(R.id.menu_search))
      .perform(click())
    onView(withId(R.id.search_src_text))
      .perform(typeText("hello"), closeSoftKeyboard(), pressBack())
    onView(withId(R.id.recycler_view))
      .check(matches(isDisplayed()))
  }

  private val dispatcher = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
      if ("/v1/trending?key=" + API_KEY == request.path) return getMockResponse("/trending_results.json")

      return MockResponse().setResponseCode(HTTP_NOT_FOUND)
    }
  }
}
