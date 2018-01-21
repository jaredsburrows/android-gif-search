package burrows.apps.example.gif.presentation.di.module

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@Module(includes = [LeakCanaryModule::class])
abstract class AppModule {
  @Binds
  abstract fun providesContext(application: Application): Context
}
