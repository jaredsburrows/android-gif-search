package com.burrowsapps.example.gif

import kotlinx.coroutines.CoroutineDispatcher

data class AppCoroutineDispatchers(
  val io: CoroutineDispatcher,
  val computation: CoroutineDispatcher,
  val main: CoroutineDispatcher
)
