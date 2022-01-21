package com.burrowsapps.example.gif.di

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.BINARY

@Retention(BINARY)
@Qualifier
annotation class DefaultDispatcher

@Retention(BINARY)
@Qualifier
annotation class IoDispatcher

@Retention(BINARY)
@Qualifier
annotation class MainDispatcher

@Retention(BINARY)
@Qualifier
annotation class MainImmediateDispatcher
