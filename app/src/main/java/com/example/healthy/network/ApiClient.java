package com.example.healthy.network;

import com.example.healthy.BuildConfig;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static Retrofit retrofitWithToken = null;
    public static String token = "";

    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxRequests(1);
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            if (BuildConfig.DEBUG) {
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            }
            OkHttpClient client = new OkHttpClient
                    .Builder()
                    .addInterceptor(interceptor)
                    .authenticator(new RSAuthenticator())
                    .addInterceptor(chain -> {
                        Request.Builder newRequest = chain.request().newBuilder();
                        newRequest.addHeader("Content-Type", "application/json;charset=UTF-8");
                        newRequest.addHeader("version", "1.0.0");
                        newRequest.addHeader("platform", "android");
                        return chain.proceed(newRequest.build());
                    })
                    .readTimeout(120, TimeUnit.SECONDS)
                    .dispatcher(dispatcher)
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .connectionSpecs(Arrays.asList(ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientWithHeader(String baseUrl) {
        if (retrofitWithToken == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxRequests(1);
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            if (BuildConfig.DEBUG) {
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            }
            OkHttpClient client = new OkHttpClient
                    .Builder()
                    .addInterceptor(interceptor)
                    .authenticator(new RSAuthenticator())
                    .addInterceptor(chain -> {
                        Request.Builder newRequest = chain.request().newBuilder();

                        if (!token.equals("")) {
                            newRequest.addHeader("Authorization", "Bearer " + token);
                            newRequest.addHeader("Content-Type", "application/json;charset=UTF-8");
                            newRequest.addHeader("version", "1.0.0");
                            newRequest.addHeader("platform", "android");
                        } else {
                            newRequest.addHeader("Content-Type", "application/json;charset=UTF-8");
                            newRequest.addHeader("version", "1.0.0");
                            newRequest.addHeader("platform", "android");
                        }
                        // TODO - add token here
                        return chain.proceed(newRequest.build());
                    })
                    .readTimeout(120, TimeUnit.SECONDS)
                    .dispatcher(dispatcher)
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .connectionSpecs(Arrays.asList(ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
                    .build();

            retrofitWithToken = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofitWithToken;
    }

    public static void clearToken() {
        retrofit = null;
        retrofitWithToken = null;
    }
}
