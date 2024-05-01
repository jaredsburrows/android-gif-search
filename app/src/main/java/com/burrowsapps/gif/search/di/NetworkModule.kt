package com.burrowsapps.gif.search.di

import android.content.Context
import com.burrowsapps.gif.search.BuildConfig.DEBUG
import com.burrowsapps.gif.search.data.api.GifService
import com.burrowsapps.gif.search.di.ApplicationMode.TESTING
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.io.File
import java.util.Date
import javax.inject.Singleton

/** Injections for the network. */
@Module
@InstallIn(SingletonComponent::class)
internal class NetworkModule {
  @Singleton
  @Provides
  fun provideGifService(retrofit: Retrofit): GifService {
    return retrofit
      .create(GifService::class.java)
  }

  @Singleton
  @Provides
  fun provideRetrofit(
    converterFactory: MoshiConverterFactory,
    client: OkHttpClient,
    baseUrl: String,
  ): Retrofit {
    return Retrofit.Builder()
      .addConverterFactory(converterFactory)
      .client(client)
      .baseUrl(baseUrl)
      .build()
  }

  @Singleton
  @Provides
  fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory {
    return MoshiConverterFactory.create(moshi)
  }

  @Singleton
  @Provides
  fun provideMoshi(): Moshi {
    return Moshi.Builder()
      .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
      .build()
  }

  @Singleton
  @Provides
  fun provideOkHttpClient(
    interceptor: HttpLoggingInterceptor,
    cache: Cache,
  ): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(interceptor)
      .followRedirects(true)
      .followSslRedirects(true)
      .retryOnConnectionFailure(true)
      .cache(cache)
      .build()
  }

  @Singleton
  @Provides
  fun provideHttpLoggingInterceptor(applicationMode: ApplicationMode): HttpLoggingInterceptor {
    return HttpLoggingInterceptor { message ->
      Timber.i(message)
    }.apply {
      level = if (applicationMode == TESTING || DEBUG) BASIC else NONE
    }
  }

  @Singleton
  @Provides
  fun provideCache(
    @ApplicationContext context: Context,
  ): Cache {
    return Cache(
      directory = File(context.cacheDir, CLIENT_CACHE_DIRECTORY),
      maxSize = CLIENT_CACHE_SIZE,
    )
  }

  private companion object {
    private const val CLIENT_CACHE_SIZE = 2 * 10 * 1024 * 1024L // 20 MiB
    private const val CLIENT_CACHE_DIRECTORY = "https-json-cache"
  }
}
