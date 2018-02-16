package com.android.retrofit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.BuildConfig;
import com.android.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;
import static okhttp3.logging.HttpLoggingInterceptor.Level.NONE;

public final class RetrofitClient {
    private static final String CACHE_CONTROL = "Cache-Control";
    private static Context mContext;
    private static RetrofitClient retofitClient;
    public static Retrofit retrofit;

    public static RetrofitClient with(Context context) {
        if (retofitClient == null)
            retofitClient = new RetrofitClient();
        mContext = context;
        return retofitClient;
    }

    public Retrofit getClient(String baseUrl) {
        retrofit = provideRetrofit(baseUrl);
        return retrofit;
    }

    private Retrofit provideRetrofit(String baseUrl) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(provideOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(provideHttpLoggingInterceptor())
                .addInterceptor(provideOfflineCacheInterceptor())
                .addNetworkInterceptor(provideCacheInterceptor())
                .dispatcher(getDispatcher())
                .cache(provideCache())
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(100, 30, TimeUnit.SECONDS))
                .retryOnConnectionFailure(true)
                .build();
    }

    private Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(mContext.getCacheDir(), "http-cache"),
                    10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cache;
    }

    private HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(@NonNull String message) {
                        if (message.contains("Date"))
                            Log.i(mContext.getString(R.string.app_name), "Api Cache storage Time: " + getCreatedAt(message.split("Date:")[1]));
                        else if (message.contains("Expires:"))
                            Log.i(mContext.getString(R.string.app_name), "Api Cache Expires Time: " + getCreatedAt(message.split("Expires:")[1]));
                        else if (message.contains("expires"))
                            Log.i(mContext.getString(R.string.app_name), message.split("expires=")[0] + "expires= " + getCreatedAt(message.split("expires=")[1].split("GMT")[0] + "GMT") + " " + message.split("expires=")[1].split("GMT")[1]);
                        else
                            Log.i(mContext.getString(R.string.app_name), message);
                    }
                });
        httpLoggingInterceptor.setLevel(BuildConfig.DEBUG ? BODY : NONE);
        return httpLoggingInterceptor;
    }

    private Dispatcher getDispatcher() {
        Dispatcher dispatcher = new Dispatcher(Executors.newFixedThreadPool(20));
        dispatcher.setMaxRequests(20);
        dispatcher.setMaxRequestsPerHost(1);
        return dispatcher;
    }

    private Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Response response = chain.proceed(chain.request().newBuilder().addHeader("User-Agent", mContext.getString(R.string.app_name)).build());
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(1, TimeUnit.DAYS)
                        .build();

                return response.newBuilder()
                        .header(CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }

    private Interceptor provideOfflineCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("User-Agent", mContext.getString(R.string.app_name)).build();
                if (!checkIfHasNetwork(mContext)) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS)
                            .build();
                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                }
                return chain.proceed(request);
            }
        };
    }


    private boolean checkIfHasNetwork(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private String getCreatedAt(String inputText) {
        SimpleDateFormat inputFormat = new SimpleDateFormat
                (" EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.getDefault());
        SimpleDateFormat inputFormat1 = new SimpleDateFormat
                ("EEE, dd-MMM-yyyy HH:mm:ss 'GMT'", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        inputFormat1.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        SimpleDateFormat outputFormat =
                new SimpleDateFormat("MMM dd, yyyy h:mm:ss");
        Date date = null;
        try {
            date = inputFormat.parse(inputText);
        } catch (ParseException e) {
            try {
                date = inputFormat1.parse(inputText);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        return outputFormat.format(date);
    }

}
