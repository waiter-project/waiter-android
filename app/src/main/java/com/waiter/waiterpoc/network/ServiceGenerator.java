package com.waiter.waiterpoc.network;

import android.util.Base64;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kharra_m on 17/02/2016.
 */
public class ServiceGenerator {

    public static final String API_BASE_URL = "http://waiter.equilibre.io:1337";
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        /*
         ** Debug log
         */
        /*
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(interceptor);
        */

        Retrofit retrofit = builder.client(httpClient.build()).build();

        return retrofit.create(serviceClass);
    }

    /*
    public static <S> S createService(Class<S> serviceClass, String email, String password) {
        if (email != null && password != null) {
            String credentials = email + ":" + password;
            final String basic = "Basic" + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            httpClient.addInterceptor()
        }
    }
    */
}
