package test;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import burrows.apps.gif.example.BuildConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.robolectric.Robolectric.flushBackgroundThreadScheduler;
import static org.robolectric.Robolectric.flushForegroundThreadScheduler;
import static org.robolectric.shadows.ShadowApplication.runBackgroundTasks;
import static org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks;

/**
 * JUnit + Robolectric Tests.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.M)
public abstract class RoboTestBase extends TestBase {
  // Android Related
  protected static final Context context = RuntimeEnvironment.application;
  protected static final Application application = RuntimeEnvironment.application;

  @Before @Override public void setUp() throws Exception {
    super.setUp();
  }

  @After @Override public void tearDown() throws Exception {
    super.tearDown();
  }

  public void finishThreads() {
    runBackgroundTasks();
    flushForegroundThreadScheduler();
    flushBackgroundThreadScheduler();
    runUiThreadTasksIncludingDelayedTasks();
  }
}
