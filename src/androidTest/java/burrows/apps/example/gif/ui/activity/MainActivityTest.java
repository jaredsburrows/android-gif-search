package burrows.apps.example.gif.ui.activity;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
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
import burrows.apps.example.gif.presentation.main.MainActivity;
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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Scanner;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
  @Rule public final MockWebServer server = new MockWebServer();
  @Rule public final ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(MainActivity.class, true, false) {
    @Override protected void beforeActivityLaunched() {
      super.beforeActivityLaunched();

      final TestApp testApp = getApplication();

      // Override app component
      final AppComponent appComponent = DaggerAppComponent.builder()
        .appModule(new AppModule(getApplication()))
        .build();
      testApp.setAppComponent(appComponent);

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
          @Override protected ImageRepository provideImageDownloader(Context context) {
            return new ImageRepository(context) {
              // Prevent Glide network call with custom override
              @Override public GifRequestBuilder<?> load(Object url) {
                return Glide.with(context)
                  .load(R.mipmap.ic_launcher)
                  .asGif()
                  .override(200, 200);
              }
            };
          }
        })
        .build();
      testApp.setRiffsyComponent(netComponent);
    }

    public TestApp getApplication() {
      return (TestApp) getTargetContext().getApplicationContext();
    }

    public Context getTargetContext() {
      return getInstrumentation().getTargetContext();
    }

    public Instrumentation getInstrumentation() {
      return InstrumentationRegistry.getInstrumentation();
    }
  };
  String mockEndPoint;

  @Before public void setUp() throws Exception {
    initMocks(this);

    mockEndPoint = server.url("/").toString();
  }

  @After public void tearDown() throws Exception {
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

  @Test public void testLoadTrendingClickOpenDialog() throws Exception {
    // Fake server response
    sendMockMessages("/trending_results.json");


    // Launch activity
    activityRule.launchActivity(new Intent(Intent.ACTION_MAIN));


    // Click and make sure dialog is shown
    onView(withId(R.id.recycler_view))
      .check(matches(isDisplayed()));

    onView(withId(R.id.recycler_view))
      .check(matches(isDisplayed()))
      .perform(actionOnItemAtPosition(0, click()));
    // Make sure dialog view is displayed
    onView(withId(R.id.gif_dialog_image))
      .check(matches(isDisplayed()));
  }

  @Test public void testLoadSearchResults() throws Exception {
    // Fake server response
    sendMockMessages("/search_results.json");


    // Launch activity
    activityRule.launchActivity(new Intent(Intent.ACTION_MAIN));


    // Open menu and search
    onView(withId(R.id.menu_search))
      .perform(click());
    // App Compat SearchView widget does not use the same id as in the regular
    // android.widget.SearchView. R.id.search_src_text is the id created by appcompat
    // search widget.
    onView(withId(R.id.search_src_text))
      .perform(typeText("cat"), closeSoftKeyboard());

    onView(withId(R.id.recycler_view))
      .check(matches(isDisplayed()));
  }

  @Test public void testLoadSearchResultsClickOpenDialog() throws Exception {
    // Fake server response
    sendMockMessages("/search_results.json");


    // Launch activity
    activityRule.launchActivity(new Intent(Intent.ACTION_MAIN));


    // Open menu and search
    onView(withId(R.id.menu_search))
      .perform(click());
    // App Compat SearchView widget does not use the same id as in the regular
    // android.widget.SearchView. R.id.search_src_text is the id created by appcompat
    // search widget.
    onView(withId(R.id.search_src_text))
      .perform(typeText("cat"), closeSoftKeyboard());

    onView(withId(R.id.recycler_view))
      .check(matches(isDisplayed()))
      .perform(actionOnItemAtPosition(0, click()));

    // Make sure dialog view is displayed
    onView(withId(R.id.gif_dialog_image))
      .check(matches(isDisplayed()));
  }

  @Test public void testLoadTrendingThenSearchThenBackToTrending() throws Exception {
    // Fake server response
    sendMockMessages("/trending_results.json");


    // Launch activity
    activityRule.launchActivity(new Intent(Intent.ACTION_MAIN));


    // Open menu and search
    onView(withId(R.id.menu_search))
      .perform(click());
    // App Compat SearchView widget does not use the same id as in the regular
    // android.widget.SearchView. R.id.search_src_text is the id created by appcompat
    // search widget.
    onView(withId(R.id.search_src_text))
      .perform(typeText("dog"), closeSoftKeyboard());

    // Go back to trending screen
    pressBack();
    onView(withId(R.id.search_close_btn))
      .perform(click());
    closeSoftKeyboard();
    onView(withId(R.id.recycler_view))
      .check(matches(isDisplayed()));
  }
}
