package burrows.apps.example.gif.di.module

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module

@Module(includes = [LeakCanaryModule::class])
abstract class AppModule {
  @Binds abstract fun providesContext(application: Application): Context
}
