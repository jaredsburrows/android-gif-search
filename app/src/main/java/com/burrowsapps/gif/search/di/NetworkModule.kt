package com.burrowsapps.gif.search.di

import android.content.Context
import android.net.TrafficStats
import android.os.Looper
import com.burrowsapps.gif.search.BuildConfig.DEBUG
import com.burrowsapps.gif.search.data.api.GifService
import com.burrowsapps.gif.search.di.ApplicationMode.TESTING
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.Date
import javax.inject.Named
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
    client: Lazy<OkHttpClient>,
    baseUrl: String,
  ): Retrofit {
    return Retrofit.Builder()
      .addConverterFactory(converterFactory)
      .callFactory { client.get().newCall(it) } // Resolve StrictMode DiskReadViolation
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
    @Named("HttpLoggingInterceptor") httpLoggingInterceptor: Interceptor,
    @Named("TrafficStatsInterceptor") trafficStatsInterceptor: Interceptor,
    cache: Cache,
  ): OkHttpClient {
    checkNotMainThread()

    return OkHttpClient.Builder()
      .addInterceptor(httpLoggingInterceptor)
      .addInterceptor(trafficStatsInterceptor)
      .cache(cache)
      .build()
  }

  // Resolve StrictMode UntaggedSocket violation
  @Named("TrafficStatsInterceptor")
  @Singleton
  @Provides
  fun provideTrafficStatsInterceptor(): Interceptor {
    return Interceptor { chain ->
      val trafficStatsTag = 0xF00D
      runCatching {
        TrafficStats.setThreadStatsTag(trafficStatsTag)
        chain.proceed(chain.request())
      }.also {
        TrafficStats.clearThreadStatsTag()
      }.getOrThrow()
    }
  }

  @Named("HttpLoggingInterceptor")
  @Singleton
  @Provides
  fun provideHttpLoggingInterceptor(applicationMode: ApplicationMode): Interceptor {
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
    checkNotMainThread()

    return Cache(context.cacheDir, CLIENT_CACHE_SIZE)
  }

  private fun checkNotMainThread() {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      throw IllegalStateException("Operation initialized on main thread.")
    }
  }

  private companion object {
    private const val CLIENT_CACHE_SIZE = 2 * 10 * 1024 * 1024L // 20 MiB
  }
}
