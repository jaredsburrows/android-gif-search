package burrows.apps.example.gif;

import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.VmPolicy;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class DebugApp extends App {
  @Override public void onCreate() {
    StrictMode.setThreadPolicy(new ThreadPolicy.Builder()
      .detectAll()
      .penaltyLog()
      .build());
    StrictMode.setVmPolicy(new VmPolicy.Builder()
      .detectAll()
      .penaltyLog()
      .build());

    super.onCreate();
  }
}
