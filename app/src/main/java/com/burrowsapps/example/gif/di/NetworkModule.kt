package com.burrowsapps.example.gif.di

import android.content.Context
import com.burrowsapps.example.gif.BuildConfig
import com.burrowsapps.example.gif.BuildConfig.DEBUG
import com.burrowsapps.example.gif.data.source.network.TenorService
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
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.io.File
import java.util.Date
import javax.inject.Singleton

/**
 * Injections for the network.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
  private const val CLIENT_CACHE_SIZE = 2 * 10 * 1024 * 1024L // 20 MiB
  private const val CLIENT_CACHE_DIRECTORY = "https-json-cache"

  @Singleton
  @Provides
  fun providesTenorService(
    retrofit: Retrofit,
  ): TenorService {
    return retrofit
      .create(TenorService::class.java)
  }

  @Singleton
  @Provides
  fun providesRetrofit(
    converterFactory: MoshiConverterFactory,
    client: OkHttpClient,
  ): Retrofit {
    return Retrofit.Builder()
      .addConverterFactory(converterFactory)
      .client(client)
      .baseUrl(BuildConfig.BASE_URL)
      .build()
  }

  @Singleton
  @Provides
  fun providesMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory {
    return MoshiConverterFactory.create(moshi)
  }

  @Singleton
  @Provides
  fun providesMoshi(): Moshi {
    return Moshi.Builder()
      .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
      .build()
  }

  @Singleton
  @Provides
  fun providesOkHttpClient(
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
  fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor { message ->
      Timber.i(message)
    }.apply {
      level = if (DEBUG) BODY else NONE
    }
  }

  @Singleton
  @Provides
  fun providesCache(@ApplicationContext context: Context): Cache {
    return Cache(
      directory = File(context.cacheDir, CLIENT_CACHE_DIRECTORY),
      maxSize = CLIENT_CACHE_SIZE,
    )
  }
}
