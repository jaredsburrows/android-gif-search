package com.burrowsapps.example.gif.giflist

import com.burrowsapps.example.gif.di.module.NetModule
import com.burrowsapps.example.gif.di.module.RiffsyModule
import dagger.Module

@Module(includes = [NetModule::class, RiffsyModule::class])
abstract class GifModule
