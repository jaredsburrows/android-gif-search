package burrows.apps.example.gif.presentation.di.module

import android.app.Application
import burrows.apps.example.gif.BuildConfig
import burrows.apps.example.gif.presentation.di.scope.PerActivity
import com.squareup.moshi.Moshi
import com.squareup.moshi.Rfc3339DateJsonAdapter
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Creates services based on Retrofit interfaces.
 *
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@Module
class NetModule {
  companion object {
    /**
     * OkHttp client request time out.
     */
    private val CLIENT_TIME_OUT = 10

    /**
     * OkHttp cache size.
     */
    private val CLIENT_CACHE_SIZE = 10 * 1024 * 1024 // 10 MiB

    /**
     * OkHttp cache directory.
     */
    private val CLIENT_CACHE_DIRECTORY = "http"
  }

  @Provides @PerActivity fun providesCache(application: Application): Cache {
    return Cache(File(application.cacheDir, CLIENT_CACHE_DIRECTORY),
      CLIENT_CACHE_SIZE.toLong())
  }

  @Provides @PerActivity fun providesMoshi(): Moshi {
    return Moshi.Builder()
      .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
      .build()
  }

  @Provides @PerActivity fun providesOkHttpClient(cache: Cache): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(HttpLoggingInterceptor()
        .setLevel(if (BuildConfig.DEBUG)
          HttpLoggingInterceptor.Level.BODY
        else
          HttpLoggingInterceptor.Level.NONE))
      .connectTimeout(CLIENT_TIME_OUT.toLong(), TimeUnit.SECONDS)
      .writeTimeout(CLIENT_TIME_OUT.toLong(), TimeUnit.SECONDS)
      .readTimeout(CLIENT_TIME_OUT.toLong(), TimeUnit.SECONDS)
      .cache(cache)
      .build()
  }

  @Provides @PerActivity fun providesRetrofit(moshi: Moshi,
                                              okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .client(okHttpClient)
      .build()
  }
}
