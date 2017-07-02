package burrows.apps.example.gif.presentation.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

import javax.inject.Singleton

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@Module
class AppModule(val application: Application) {
  @Provides @Singleton fun providesApplication(): Application {
    return application
  }

  @Provides @Singleton fun providesApplicationContext(): Context {
    return application.applicationContext
  }
}
