package burrows.apps.example.gif.di.module;

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
public final class LeakCanaryModule {
  @Provides @Singleton RefWatcher provideRefWatcher(Application application) {
    return LeakCanary.install(application);
  }
}
