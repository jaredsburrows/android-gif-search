package com.burrowsapps.gif.search.di

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.data.api.GifService
import com.burrowsapps.gif.search.di.ApplicationMode.TESTING
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Date

@RunWith(AndroidJUnit4::class)
class NetworkModuleTest {
  private lateinit var networkModule: NetworkModule
  private lateinit var context: Context

  @Before
  fun setUp() {
    networkModule = NetworkModule()
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun provideGifServiceReturnsGifService() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl("https://example.com")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val gifService = networkModule.provideGifService(retrofit)

    assertThat(gifService).isInstanceOf(GifService::class.java)
  }

  @Test
  fun provideRetrofitReturnsRetrofit() {
    val moshi =
      Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .build()

    val converterFactory = MoshiConverterFactory.create(moshi)
    val client = OkHttpClient.Builder().build()
    val lazyClient = dagger.Lazy { client }
    val retrofit = networkModule.provideRetrofit(converterFactory, lazyClient, "https://example.com")

    assertThat(retrofit.baseUrl().toString()).isEqualTo("https://example.com/")
  }

  @Test
  fun provideMoshiConverterFactoryReturnsMoshiConverterFactory() {
    val moshi = Moshi.Builder().build()

    val factory = networkModule.provideMoshiConverterFactory(moshi)

    assertThat(factory).isInstanceOf(MoshiConverterFactory::class.java)
  }

  @Test
  fun provideMoshiReturnsMoshi() {
    val moshi = networkModule.provideMoshi()

    assertThat(moshi.adapter(Date::class.java)).isNotNull()
  }

  @Test
  fun provideOkHttpClientReturnsOkHttpClient() =
    runTest {
      runBlocking {
        withContext(IO) {
          val loggingInterceptor = networkModule.provideHttpLoggingInterceptor(TESTING)
          val trafficStatsInterceptor = networkModule.provideTrafficStatsInterceptor()
          val cache = networkModule.provideCache(context)

          val client = networkModule.provideOkHttpClient(loggingInterceptor, trafficStatsInterceptor, cache)

          assertThat(client.cache).isEqualTo(cache)
          assertThat(client.interceptors).contains(loggingInterceptor)
          assertThat(client.interceptors).contains(trafficStatsInterceptor)
        }
      }
    }

  @Test
  fun provideTrafficStatsInterceptorReturnsInterceptor() {
    val interceptor = networkModule.provideTrafficStatsInterceptor()

    assertThat(interceptor).isNotNull()
  }

  @Test
  fun provideHttpLoggingInterceptorReturnsHttpLoggingInterceptor() {
    val interceptor = networkModule.provideHttpLoggingInterceptor(TESTING)

    assertThat(interceptor).isInstanceOf(HttpLoggingInterceptor::class.java)
    assertThat((interceptor as HttpLoggingInterceptor).level).isEqualTo(HttpLoggingInterceptor.Level.BASIC)
  }

  @Test
  fun provideCacheReturnsCache() =
    runTest {
      runBlocking {
        withContext(IO) {
          val cache = networkModule.provideCache(context)

          assertThat(cache.directory).isEqualTo(context.cacheDir)
          assertThat(cache.maxSize()).isEqualTo(20 * 1024 * 1024L)
        }
      }
    }
}
