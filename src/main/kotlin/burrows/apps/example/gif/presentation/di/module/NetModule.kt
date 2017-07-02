package burrows.apps.example.gif.presentation.di.module

import android.app.Application
import burrows.apps.example.gif.BuildConfig
import burrows.apps.example.gif.presentation.di.scope.PerActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

import java.io.File
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
     * OkHttp request date format. Eg. 2016-06-19T13:07:45.139Z
     */
    private val CLIENT_DATE_FORMAT = "yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'"

    /**
     * OkHttp cache directory.
     */
    private val CLIENT_CACHE_DIRECTORY = "http"
  }

  @Provides @PerActivity fun providesCache(application: Application): Cache {
    return Cache(File(application.cacheDir, CLIENT_CACHE_DIRECTORY),
      CLIENT_CACHE_SIZE.toLong())
  }

  @Provides @PerActivity fun providesGson(): Gson {
    return GsonBuilder()
      .setDateFormat(CLIENT_DATE_FORMAT)
      .create()
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

  @Provides @PerActivity fun providesRetrofitBuilder(gson: Gson,
                                                     okHttpClient: OkHttpClient): Retrofit.Builder {
    return Retrofit.Builder()
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create(gson))
      .client(okHttpClient)
  }
}
