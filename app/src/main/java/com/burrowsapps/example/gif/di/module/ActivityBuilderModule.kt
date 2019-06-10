package com.burrowsapps.example.gif.di.module

import com.burrowsapps.example.gif.giflist.GifActivity
import com.burrowsapps.example.gif.giflist.GifModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {
  @ContributesAndroidInjector(modules = [GifModule::class])
  abstract fun provideMainActivity(): GifActivity
}
