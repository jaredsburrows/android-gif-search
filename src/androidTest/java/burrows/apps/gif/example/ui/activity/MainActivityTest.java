package burrows.apps.gif.example.ui.activity;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import burrows.apps.gif.example.R;
import burrows.apps.gif.example.TestApp;
import burrows.apps.gif.example.di.component.AppComponent;
import burrows.apps.gif.example.di.component.DaggerAppComponent;
import burrows.apps.gif.example.di.component.DaggerRiffsyComponent;
import burrows.apps.gif.example.di.component.RiffsyComponent;
import burrows.apps.gif.example.di.module.AppModule;
import burrows.apps.gif.example.di.module.LeakCanaryModule;
import burrows.apps.gif.example.di.module.RiffsyModule;
import burrows.apps.gif.example.rest.service.RiffsyService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
        .leakCanaryModule(new LeakCanaryModule(getApplication()))
        .build();
      testApp.setAppComponent(appComponent);

      // Override service component
      final RiffsyComponent riffsyComponent = DaggerRiffsyComponent.builder()
        .appComponent(appComponent)
        .riffsyModule(new RiffsyModule() {
          @Override protected RiffsyService provideRiffsyService() {
            return new RiffsyService(mockEndPoint);
          }
        })
        .build();
      testApp.setRiffsyComponent(riffsyComponent);
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
  protected String mockEndPoint;

  @Before public void setUp() throws Exception {
    mockEndPoint = server.url("/").toString();
  }

  @Test public void testLoadTrendingClickOpenDialog() {
    // Fake server response
    final String mockResponse = new Scanner(getClass().getResourceAsStream("/trending_results.json"),
      Charset.defaultCharset().name()).useDelimiter("\\A").next();
    server.enqueue(new MockResponse()
      .setResponseCode(HttpURLConnection.HTTP_OK)
      .setBody(mockResponse));


    // Launch activity
    activityRule.launchActivity(new Intent(Intent.ACTION_MAIN));


    // Click and make sure dialog is shown
    onView(ViewMatchers.withId(R.id.recycler_view))
      .check(matches(isDisplayed()))
      .perform(actionOnItemAtPosition(0, click()));
    // Make sure dialog view is displayed
    onView(withId(R.id.gif_dialog_image))
      .check(matches(isDisplayed()));
  }

  @Test public void testLoadSearchResults() {
    // Fake server response
    final String mockResponse = new Scanner(getClass().getResourceAsStream("/search_results.json"),
      Charset.defaultCharset().name()).useDelimiter("\\A").next();
    server.enqueue(new MockResponse()
      .setResponseCode(HttpURLConnection.HTTP_OK)
      .setBody(mockResponse));


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

  @Test public void testLoadSearchResultsClickOpenDialog() {
    // Fake server response
    final String mockResponse = new Scanner(getClass().getResourceAsStream("/search_results.json"),
      Charset.defaultCharset().name()).useDelimiter("\\A").next();
    server.enqueue(new MockResponse()
      .setResponseCode(HttpURLConnection.HTTP_OK)
      .setBody(mockResponse));


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

  @Test public void testLoadTrendingThenSearchThenBackToTrending() {
    // Fake server response
    final String mockResponse = new Scanner(getClass().getResourceAsStream("/trending_results.json"),
      Charset.defaultCharset().name()).useDelimiter("\\A").next();
    server.enqueue(new MockResponse()
      .setResponseCode(HttpURLConnection.HTTP_OK)
      .setBody(mockResponse));


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
