package test;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@RunWith(AndroidJUnit4.class)
public abstract class AndroidTestBase<T extends Activity> extends TestBase {
  protected Context context;
  protected Activity activity;
  protected Resources resources;
  @Rule public ActivityTestRule<T> activityRule;

  public AndroidTestBase(Class<T> activityClass) {
    activityRule = getActivityRule(activityClass);
  }

  @Before @Override public void setUp() throws Exception {
    super.setUp();

    context = getInstrumentation().getTargetContext();
    activity = activityRule.getActivity();
    resources = context.getResources();

    // Allows us to mock classes
    System.setProperty("dexmaker.dexcache", context.getCacheDir().getPath());

    keepScreenOn();
    grantPhonePermissions();

    Intents.init();
  }

  @After @Override public void tearDown() throws Exception {
    super.tearDown();

    Intents.release();
  }

  protected ActivityTestRule<T> getActivityRule(Class<T> activityClass) {
    return new ActivityTestRule<>(activityClass);
  }

  /**
   * Add permissions such as Manifest.permission.ACCESS_FINE_LOCATION.
   */
  protected List<String> permissions() {
    return new ArrayList<>();
  }

  private void keepScreenOn() {
    final T activity = activityRule.getActivity();
    final Runnable wakeUpDevice = () -> activity.getWindow().addFlags(FLAG_TURN_SCREEN_ON
      | FLAG_SHOW_WHEN_LOCKED
      | FLAG_KEEP_SCREEN_ON);
    activity.runOnUiThread(wakeUpDevice);
  }

  private void grantPhonePermissions() {
    // In M+, trying to call a number will trigger a runtime dialog. Make sure
    // the permission is granted before running this test.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      for (String permission : permissions()) {
        getInstrumentation().getUiAutomation().executeShellCommand(
          "pm grant " + getTargetContext().getPackageName() + " " + permission);
      }
    }
  }
}
