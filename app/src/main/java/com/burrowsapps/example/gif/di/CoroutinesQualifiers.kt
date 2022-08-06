package com.burrowsapps.example.gif.di

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.BINARY

@Retention(BINARY)
@Qualifier
internal annotation class DefaultDispatcher

@Retention(BINARY)
@Qualifier
internal annotation class IoDispatcher

@Retention(BINARY)
@Qualifier
internal annotation class MainDispatcher

@Retention(BINARY)
@Qualifier
internal annotation class MainImmediateDispatcher
