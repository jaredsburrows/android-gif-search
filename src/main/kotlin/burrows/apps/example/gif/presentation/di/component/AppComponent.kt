package burrows.apps.example.gif.presentation.di.component

import android.app.Application
import android.content.Context
import burrows.apps.example.gif.App
import burrows.apps.example.gif.presentation.di.module.AppModule
import dagger.Component

import javax.inject.Singleton

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
  // Injections
  fun inject(app: App)

  // Expose to subgraphs
  fun application(): Application
  fun context(): Context

  // Setup components dependencies and modules
  companion object {
    fun init(application: Application): AppComponent {
      return DaggerAppComponent.builder()
        .appModule(AppModule(application))
        .build()
    }
  }
}
