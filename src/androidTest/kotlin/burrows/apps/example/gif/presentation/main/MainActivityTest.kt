package burrows.apps.example.gif.presentation.main

import android.content.Context
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
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import burrows.apps.example.gif.R
import burrows.apps.example.gif.data.rest.repository.ImageRepository
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
import test.CustomTestRule
import java.io.IOException
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
  @Rule @JvmField val activityRule: CustomTestRule<MainActivity> = object : CustomTestRule<MainActivity>(MainActivity::class.java, true, false) {
    override fun beforeActivityLaunched() {
      super.beforeActivityLaunched()

      val app = application

      // Override app component
      val appComponent = DaggerAppComponent.builder()
        .appModule(AppModule(application))
        .build()
      app.appComponent = appComponent

      // Override service component
      val netComponent = DaggerActivityComponent.builder()
        .appComponent(appComponent)
        .riffsyModule(object : RiffsyModule() {
          // Set custom endpoint for rest service
          override fun provideRiffsyApi(builder: Retrofit.Builder): RiffsyApiClient {
            return builder.baseUrl(mockEndPoint!!)
              .build()
              .create(RiffsyApiClient::class.java)
          }
        })
        .glideModule(object : GlideModule() {
          override fun provideImageDownloader(context: Context): ImageRepository {
            return object : ImageRepository(context) {
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
      app.setRiffsyComponent(netComponent)
    }
  }

  @Rule val server = MockWebServer()
  private val dispatcher = object : Dispatcher() {
    @Throws(InterruptedException::class)
    override fun dispatch(request: RecordedRequest): MockResponse {
      if ("/v1/trending?key=" + API_KEY == request.path) {
        return getMockResponse("/trending_results.json")
      }

      return MockResponse().setResponseCode(HTTP_NOT_FOUND)
    }
  }
  private var mockEndPoint: String? = null

  @Before @Throws(Throwable::class)
  override fun setUp() {
    super.setUp()

    initMocks(this)

    mockEndPoint = server.url("/").toString()
    server.setDispatcher(dispatcher)
  }

  @After @Throws(Throwable::class)
  override fun tearDown() {
    super.tearDown()

    server.shutdown()
  }

  private fun getMockResponse(fileName: String): MockResponse {
    val stream = javaClass.getResourceAsStream(fileName)
    val mockResponse = Scanner(stream, Charset.defaultCharset().name())
      .useDelimiter("\\A").next()

    val response = MockResponse().setResponseCode(HTTP_OK)
      .setBody(mockResponse)

    try {
      stream.close()
    } catch (ignore: IOException) {
    }

    return response
  }

  @Ignore
  @Test
  @Throws(Exception::class)
  fun testTrendingThenClickOpenDialog() {
    // Act
    activityRule.launchActivity()
    activityRule.keepScreenOn()

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

  @Test @Throws(Exception::class)
  fun testTrendingResultsThenSearchThenBackToTrending() {
    // Act
    activityRule.launchActivity()
    activityRule.keepScreenOn()

    // Assert
    onView(withId(R.id.menu_search))
      .perform(click())
    onView(withId(R.id.search_src_text))
      .perform(typeText("hello"), closeSoftKeyboard(), pressBack())
    onView(withId(R.id.recycler_view))
      .check(matches(isDisplayed()))
  }
}
