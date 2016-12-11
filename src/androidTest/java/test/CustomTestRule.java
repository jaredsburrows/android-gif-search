package test;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import burrows.apps.example.gif.TestApp;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class CustomTestRule<T extends Activity> extends ActivityTestRule<T> {
  public CustomTestRule(Class<T> activityClass) {
    super(activityClass);
  }

  public CustomTestRule(Class<T> activityClass, boolean initialTouchMode) {
    super(activityClass, initialTouchMode);
  }

  public CustomTestRule(Class<T> activityClass, boolean initialTouchMode, boolean launchActivity) {
    super(activityClass, initialTouchMode, launchActivity);
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

  public void launchActivity() {
    launchActivity(new Intent(Intent.ACTION_MAIN));
  }

  // Lambda causes test to fail here, expanded for now
  public void keepScreenOn() {
    final Activity activity = getActivity();
    final Runnable wakeUpDevice = new Runnable() {
      @Override public void run() {
        activity.getWindow().addFlags(FLAG_TURN_SCREEN_ON
          | FLAG_SHOW_WHEN_LOCKED
          | FLAG_KEEP_SCREEN_ON);
      }
    };
    activity.runOnUiThread(wakeUpDevice);
  }
}
