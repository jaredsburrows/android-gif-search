package test;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@RunWith(AndroidJUnit4.class)
public abstract class AndroidTestBase<T extends Activity> extends TestBase {
    protected Context mContext;
    protected Activity mActivity;
    protected Resources mResources;
    @Rule public ActivityTestRule<T> mActivityRule;

    public AndroidTestBase(final Class<T> activityClass) {
        this.mActivityRule = this.getActivityRule(activityClass);
    }

    @Before @Override public void setUp() throws Exception {
        super.setUp();

        this.mContext = getInstrumentation().getTargetContext();
        this.mActivity = this.mActivityRule.getActivity();
        this.mResources = this.mContext.getResources();

        // Allows us to mock classes
        System.setProperty("dexmaker.dexcache", this.mContext.getCacheDir().getPath());

        this.keepScreenOn();
        this.grantPhonePermissions();

        Intents.init();
    }

    @After @Override public void tearDown() throws Exception {
        super.tearDown();

        Intents.release();
    }

    protected ActivityTestRule<T> getActivityRule(final Class<T> activityClass) {
        return new ActivityTestRule<>(activityClass);
    }

    /**
     * Add permissions such as Manifest.permission.ACCESS_FINE_LOCATION.
     */
    protected List<String> permissions() {
        return new ArrayList<>();
    }

    private void keepScreenOn() {
        final T activity = this.mActivityRule.getActivity();
        final Runnable wakeUpDevice = new Runnable() {
            @Override
            public void run() {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        };
        activity.runOnUiThread(wakeUpDevice);
    }

    private void grantPhonePermissions() {
        // In M+, trying to call a number will trigger a runtime dialog. Make sure
        // the permission is granted before running this test.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (final String permission : this.permissions()) {
                getInstrumentation().getUiAutomation().executeShellCommand(
                        "pm grant " + getTargetContext().getPackageName() + " " + permission);
            }
        }
    }
}
