package burrows.apps.example.gif

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy

class DebugApp : App() {
  override fun onCreate() {
    StrictMode.setThreadPolicy(ThreadPolicy.Builder()
      .detectAll()
      .penaltyLog()
      .build())
    StrictMode.setVmPolicy(VmPolicy.Builder()
      .detectAll()
      .penaltyLog()
      .build())

    super.onCreate()
  }
}
