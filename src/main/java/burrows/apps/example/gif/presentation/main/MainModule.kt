package burrows.apps.example.gif.presentation.main

import burrows.apps.example.gif.di.module.LeakCanaryModule
import burrows.apps.example.gif.di.module.NetModule
import burrows.apps.example.gif.di.module.RiffsyModule
import dagger.Binds
import dagger.Module

@Module(includes = [NetModule::class, RiffsyModule::class, LeakCanaryModule::class])
abstract class MainModule {
  @Binds abstract fun providesPresenter(presenter: MainContract.Presenter): MainContract.Presenter
  @Binds abstract fun providesView(mainActivity: MainActivity): MainContract.View
}
