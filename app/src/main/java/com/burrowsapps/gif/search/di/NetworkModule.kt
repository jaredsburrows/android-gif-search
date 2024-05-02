package com.burrowsapps.gif.search.di

import android.content.Context
import android.net.TrafficStats
import android.util.Log
import androidx.core.net.TrafficStatsCompat
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
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.io.IOException
import java.util.Date
import javax.inject.Named
import javax.inject.Singleton


/** Injections for the network. */
@Module
@InstallIn(SingletonComponent::class)
internal class NetworkModule {
  @Singleton
  @Provides
  fun provideGifService(retrofit: Lazy<Retrofit>): GifService {
    return retrofit.get().create(GifService::class.java)
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
      .client(client.get())
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
    applicationMode: ApplicationMode,
  ): OkHttpClient {
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
    return object : Interceptor {
      override fun intercept(chain: Interceptor.Chain): Response {
        val trafficStatsTag = 0xF00D
        try {
          TrafficStats.setThreadStatsTag(trafficStatsTag)
          return chain.proceed(chain.request())
        } finally {
          TrafficStats.clearThreadStatsTag()
        }
      }
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
    return Cache(
      context.cacheDir, // Use the default cache directory
      CLIENT_CACHE_SIZE,
    )
  }

  private companion object {
    private const val CLIENT_CACHE_SIZE = 2 * 10 * 1024 * 1024L // 20 MiB
  }
}
