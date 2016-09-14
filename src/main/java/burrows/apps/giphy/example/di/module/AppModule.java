package burrows.apps.giphy.example.di.module;


import android.app.Application;
import android.content.Context;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@Module
public class AppModule {
  private Application application;

  public AppModule(final Application application) {
    this.application = application;
  }

  @Provides @Singleton Application provideApplication() {
    return application;
  }

  @Provides @Singleton Context provideApplicationContext() {
    return application.getApplicationContext();
  }
}
