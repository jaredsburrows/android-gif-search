package com.burrowsapps.example.gif

import kotlinx.coroutines.CoroutineDispatcher

interface BaseCoroutineDispatcherProvider {
  fun io(): CoroutineDispatcher
  fun ui(): CoroutineDispatcher
}
