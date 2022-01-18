package com.burrowsapps.example.gif

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

open class CoroutineDispatcherProvider @Inject constructor() : BaseCoroutineDispatcherProvider {
  override fun io(): CoroutineDispatcher = Dispatchers.IO
  override fun ui(): CoroutineDispatcher = Dispatchers.Main
}
