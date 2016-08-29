package burrows.apps.giphy.example.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Creates services based on Retrofit interfaces.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class ServiceUtil {
    /**
     * HTTP client request time out.
     */
    private static final int CLIENT_TIME_OUT = 10;

    /**
     * Date format for requests. Eg. 2016-06-19T13:07:45.139Z
     */
    private static final String DATE_FORMAT = "yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'";

    private ServiceUtil() {
    }

    /**
     * Return new Service.
     *
     * @param clazz    Interface for Service.
     * @param endPoint Endpoint for Service.
     * @param <T>      Service type.
     * @return Instance of new Service.
     */
    public static <T> T createService(final Class<T> clazz, final String endPoint) {
        return getRetrofit(endPoint).create(clazz);
    }

    /**
     * Creates custom Retrofit instance with RxJava support, Gson support and OkHttp supprt.
     *
     * @param endPoint Endpoint of service.
     * @return Custom instance of Retrofit.
     */
    private static Retrofit getRetrofit(final String endPoint) {
        return new Retrofit.Builder()
                .baseUrl(endPoint)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .client(getOkHttpClient())
                .build();
    }

    /**
     * Creates custom Gson instance with custom date format.
     *
     * @return Custom instance of Gson.
     */
    private static Gson getGson() {
        return new GsonBuilder()
                .setDateFormat(DATE_FORMAT)
                .create();
    }

    /**
     * Creates custom OkHttpClient instance with logging and connect/write/read timeouts.
     *
     * @return Custom instance of OkHttpClient.
     */
    private static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
                .build();
    }
}
