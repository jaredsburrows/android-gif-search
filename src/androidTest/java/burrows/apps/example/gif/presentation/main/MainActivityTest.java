package burrows.apps.example.gif.presentation.main;

import android.content.Context;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import burrows.apps.example.gif.R;
import burrows.apps.example.gif.TestApp;
import burrows.apps.example.gif.data.rest.repository.ImageRepository;
import burrows.apps.example.gif.data.rest.repository.RiffsyRepository;
import burrows.apps.example.gif.presentation.di.component.ActivityComponent;
import burrows.apps.example.gif.presentation.di.component.AppComponent;
import burrows.apps.example.gif.presentation.di.component.DaggerActivityComponent;
import burrows.apps.example.gif.presentation.di.component.DaggerAppComponent;
import burrows.apps.example.gif.presentation.di.module.AppModule;
import burrows.apps.example.gif.presentation.di.module.GlideModule;
import burrows.apps.example.gif.presentation.di.module.RiffsyModule;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import retrofit2.Retrofit;
import test.AndroidTestBase;
import test.CustomTestRule;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Scanner;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@SuppressWarnings("unchecked")
@SmallTest @RunWith(AndroidJUnit4.class)
public final class MainActivityTest extends AndroidTestBase {
  @Rule public final CustomTestRule<MainActivity> activityRule = new CustomTestRule<MainActivity>(MainActivity.class, true, false) {
    @Override protected void beforeActivityLaunched() {
      super.beforeActivityLaunched();

      final TestApp app = getApplication();

      // Override app component
      final AppComponent appComponent = DaggerAppComponent.builder()
        .appModule(new AppModule(getApplication()))
        .build();
      app.setAppComponent(appComponent);

      // Override service component
      final ActivityComponent netComponent = DaggerActivityComponent.builder()
        .appComponent(appComponent)
        .riffsyModule(new RiffsyModule() {
          // Set custom endpoint for rest service
          @Override protected RiffsyRepository provideRiffsyApi(Retrofit.Builder retrofit) {
            return retrofit.baseUrl(mockEndPoint).build().create(RiffsyRepository.class);
          }
        })
        .glideModule(new GlideModule() {
          @Override protected ImageRepository provideImageDownloader(final Context context) {
            return new ImageRepository(context) {
              // Prevent Glide network call with custom override
              @Override public <T> GifRequestBuilder<T> load(T url) {
                return (GifRequestBuilder<T>) Glide.with(context)
                  .load(R.mipmap.ic_launcher)
                  .asGif()
                  .placeholder(R.mipmap.ic_launcher)
                  .fallback(R.mipmap.ic_launcher)
                  .error(R.mipmap.ic_launcher)
                  .override(200, 200);
              }
            };
          }
        })
        .build();
      app.setRiffsyComponent(netComponent);
    }
  };
  @Rule public final MockWebServer server = new MockWebServer();
  String mockEndPoint;

  @Before @Override public void setUp() throws Exception {
    super.setUp();

    initMocks(this);

    mockEndPoint = server.url("/").toString();
  }

  @After @Override public void tearDown() throws Exception {
    super.tearDown();

    server.shutdown();
  }

  private void sendMockMessages(String fileName) throws Exception {
    final InputStream stream = getClass().getResourceAsStream(fileName);
    final String mockResponse = new Scanner(stream, Charset.defaultCharset().name())
      .useDelimiter("\\A").next();

    server.enqueue(new MockResponse()
      .setResponseCode(HttpURLConnection.HTTP_OK)
      .setBody(mockResponse));

    stream.close();
  }

  @Test public void testTrendingThenClickOpenDialog() throws Exception {
    // Fake server response
    sendMockMessages("/trending_results.json");

    // Launch activity
    activityRule.launchActivity();
    activityRule.keepScreenOn();

    // Click and make sure dialog is shown
    onView(withId(R.id.recycler_view))
      .perform(actionOnItemAtPosition(0, click())); // Select 0, the response only contains 1 item

    // Assert
    onView(withId(R.id.gif_dialog_image))
      .inRoot(isDialog())
      .check(matches(isDisplayed()));
  }

  @Test public void testTrendingResultsThenSearchThenBackToTrending() throws Exception {
    // Fake server response
    sendMockMessages("/trending_results.json");

    // Launch activity
    activityRule.launchActivity();
    activityRule.keepScreenOn();

    // Open menu
    onView(withId(R.id.menu_search))
      .perform(click());

    // Type in search bar
    onView(withId(R.id.search_src_text))
      .perform(typeText("hello"), closeSoftKeyboard(), pressBack());

    // Assert
    onView(withId(R.id.recycler_view))
      .check(matches(isDisplayed()));
  }
}
