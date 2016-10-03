package burrows.apps.gif.example.di.module;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@Module
public class LeakCanaryModule {
  private final Application application;

  public LeakCanaryModule(Application application) {
    this.application = application;
  }

  @Provides @Singleton RefWatcher provideRefWatcher() {
    return LeakCanary.install(application);
  }
}
