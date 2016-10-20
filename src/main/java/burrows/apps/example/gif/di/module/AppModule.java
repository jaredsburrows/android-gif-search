package burrows.apps.example.gif.di.module;


import android.app.Application;
import android.content.Context;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@Module
public final class AppModule {
  private final Application application;

  public AppModule(Application application) {
    this.application = application;
  }

  @Provides @Singleton Application provideApplication() {
    return application;
  }

  @Provides @Singleton Context provideApplicationContext() {
    return application.getApplicationContext();
  }
}
