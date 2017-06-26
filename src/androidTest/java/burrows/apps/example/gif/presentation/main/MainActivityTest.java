package burrows.apps.example.gif.presentation.main;

import android.content.Context;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import burrows.apps.example.gif.R;
import burrows.apps.example.gif.TestApp;
import burrows.apps.example.gif.data.rest.repository.ImageRepository;
import burrows.apps.example.gif.data.rest.repository.RiffsyApiClient;
import burrows.apps.example.gif.presentation.di.component.ActivityComponent;
import burrows.apps.example.gif.presentation.di.component.AppComponent;
import burrows.apps.example.gif.presentation.di.component.DaggerActivityComponent;
import burrows.apps.example.gif.presentation.di.component.DaggerAppComponent;
import burrows.apps.example.gif.presentation.di.module.AppModule;
import burrows.apps.example.gif.presentation.di.module.GlideModule;
import burrows.apps.example.gif.presentation.di.module.RiffsyModule;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import java.io.IOException;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import retrofit2.Retrofit;
import test.AndroidTestBase;
import test.CustomTestRule;

import java.io.InputStream;
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
import static burrows.apps.example.gif.data.rest.repository.RiffsyApiClient.API_KEY;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@SuppressWarnings("unchecked")
@SmallTest @RunWith(AndroidJUnit4.class)
public final class MainActivityTest extends AndroidTestBase {
  @Rule public final CustomTestRule<MainActivity> activityRule =
    new CustomTestRule<MainActivity>(MainActivity.class, true, false) {
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
            @Override public RiffsyApiClient provideRiffsyApi(Retrofit.Builder builder) {
              return builder.baseUrl(mockEndPoint)
                .build()
                .create(RiffsyApiClient.class);
            }
          })
          .glideModule(new GlideModule() {
            @Override public ImageRepository provideImageDownloader(final Context context) {
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
  private final Dispatcher dispatcher = new Dispatcher() {
    @Override public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
      if (("/v1/trending?key=" + API_KEY).equals(request.getPath())) {
        return getMockResponse("/trending_results.json");
      }

      return new MockResponse().setResponseCode(HTTP_NOT_FOUND);
    }
  };
  private String mockEndPoint;

  @Before @Override public void setUp() throws Throwable {
    super.setUp();

    initMocks(this);

    mockEndPoint = server.url("/").toString();
    server.setDispatcher(dispatcher);
  }

  @After @Override public void tearDown() throws Throwable {
    super.tearDown();

    server.shutdown();
  }

  private MockResponse getMockResponse(String fileName) {
    final InputStream stream = getClass().getResourceAsStream(fileName);
    final String mockResponse = new Scanner(stream, Charset.defaultCharset().name())
      .useDelimiter("\\A").next();

    final MockResponse response = new MockResponse().setResponseCode(HTTP_OK)
      .setBody(mockResponse);

    try {
      stream.close();
    } catch (IOException ignore) {
    }

    return response;
  }

  @Ignore
  @Test public void testTrendingThenClickOpenDialog() throws Exception {
    // Act
    activityRule.launchActivity();
    activityRule.keepScreenOn();

    // Assert
    onView(withId(R.id.recycler_view))
      .perform(actionOnItemAtPosition(0, click())); // Select 0, the response only contains 1 item
    onView(withId(R.id.gif_dialog_image))
      .inRoot(isDialog())
      .check(matches(isDisplayed()));
    onView(withId(R.id.gif_dialog_image))
      .inRoot(isDialog())
      .perform(pressBack());
  }

  @Test public void testTrendingResultsThenSearchThenBackToTrending() throws Exception {
    // Act
    activityRule.launchActivity();
    activityRule.keepScreenOn();

    // Assert
    onView(withId(R.id.menu_search))
      .perform(click());
    onView(withId(R.id.search_src_text))
      .perform(typeText("hello"), closeSoftKeyboard(), pressBack());
    onView(withId(R.id.recycler_view))
      .check(matches(isDisplayed()));
  }
}
