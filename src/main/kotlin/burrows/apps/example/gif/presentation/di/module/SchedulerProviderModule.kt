package burrows.apps.example.gif.presentation.di.module

import burrows.apps.example.gif.presentation.SchedulerProvider
import burrows.apps.example.gif.presentation.di.scope.PerActivity
import dagger.Module
import dagger.Provides

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@Module
class SchedulerProviderModule {
  @Provides @PerActivity internal fun providerSchedulerProvider(): SchedulerProvider {
    return SchedulerProvider()
  }
}
