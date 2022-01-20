package com.burrowsapps.example.gif

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

open class CoroutineDispatcherProvider @Inject constructor() : BaseCoroutineDispatcherProvider {
  override fun io(): CoroutineDispatcher = IO
  override fun ui(): CoroutineDispatcher = Main
}
