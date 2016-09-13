package burrows.apps.giphy.example;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.VmPolicy;
import android.support.v7.app.AppCompatDelegate;
import burrows.apps.giphy.example.rx.RxBus;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class App extends Application {
    private static RxBus sBus;
    private RefWatcher mRefWatcher;

    @Override public void onCreate() {
        super.onCreate();

        // Let's start paying critical attention to issues via Logcat
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
            StrictMode.setVmPolicy(new VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
        }

        sBus = new RxBus();

        // Initialize LeakCanary for memory analysis, removed on release
        this.mRefWatcher = LeakCanary.install(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static RxBus getBus() {
        if (sBus == null) {
            return new RxBus();
        }
        return sBus;
    }

    /**
     * Use this method to watch the reference to context in onDestroy methods.
     *
     * @param context Context.
     * @return Instance of RefWatcher.
     */
    public static RefWatcher getRefWatcher(final Context context) {
        return ((App) context.getApplicationContext()).mRefWatcher;
    }
}
