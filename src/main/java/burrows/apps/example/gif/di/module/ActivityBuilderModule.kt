package burrows.apps.example.gif.di.module

import burrows.apps.example.gif.presentation.main.MainActivity
import burrows.apps.example.gif.presentation.main.MainModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {
  @ContributesAndroidInjector(modules = [MainModule::class])
  abstract fun providesMainActivity(): MainActivity
}
