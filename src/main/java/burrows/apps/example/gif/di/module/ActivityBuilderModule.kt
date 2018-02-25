package burrows.apps.example.gif.di.module

import burrows.apps.example.gif.giflist.GifActivity
import burrows.apps.example.gif.giflist.GifModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {
  @ContributesAndroidInjector(modules = [GifModule::class])
  abstract fun providesMainActivity(): GifActivity
}
