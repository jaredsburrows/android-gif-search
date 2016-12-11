package burrows.apps.example.gif.presentation.di.module;

import android.app.Application;
import burrows.apps.example.gif.App;
import burrows.apps.example.gif.BuildConfig;
import burrows.apps.example.gif.presentation.di.scope.PerActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Creates services based on Retrofit interfaces.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
@Module
public final class NetModule {
  /**
   * OkHttp client request time out.
   */
  private static final int CLIENT_TIME_OUT = 10;

  /**
   * OkHttp cache size.
   */
  private static final int CLIENT_CACHE_SIZE = 10 * 1024 * 1024; // 10 MiB

  /**
   * OkHttp request date format. Eg. 2016-06-19T13:07:45.139Z
   */
  private static final String CLIENT_DATE_FORMAT = "yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'";

  /**
   * OkHttp cache directory.
   */
  private static final String CLIENT_CACHE_DIRECTORY = "http";

  @Provides @PerActivity Cache provideCache(Application application) {
    return new Cache(new File(application.getCacheDir(), CLIENT_CACHE_DIRECTORY), CLIENT_CACHE_SIZE);
  }

  @Provides @PerActivity Gson provideGson() {
    return new GsonBuilder()
      .setDateFormat(CLIENT_DATE_FORMAT)
      .create();
  }

  @Provides @PerActivity OkHttpClient provideOkHttpClient(Cache cache) {
    return new OkHttpClient.Builder()
      .addInterceptor(new HttpLoggingInterceptor()
        .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY: HttpLoggingInterceptor.Level.NONE))
      .connectTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
      .writeTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
      .readTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
      .cache(cache)
      .build();
  }

  @Provides @PerActivity Retrofit.Builder provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
    return new Retrofit.Builder()
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create(gson))
      .client(okHttpClient);
  }
}
