package com.waiter.network;

import com.waiter.Utils;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientGenerator {

    private static final String API_BASE_URL = setApiBaseUrl();
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static String setApiBaseUrl() {
        if (Utils.isEmulator()) {
            return "http://10.0.2.2:5000";
        }
        return "http://192.168.1.9:5000";
    }

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createClient(Class<S> clientClass) {
        /*
         ** Debug log
         */
        /*
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(interceptor);
        */

        Retrofit retrofit = builder.client(httpClient.build()).build();

        return retrofit.create(clientClass);
    }

    /*
    public static <S> S createClient(Class<S> clientClass, String email, String password) {
        if (email != null && password != null) {
            String credentials = email + ":" + password;
            final String basic = "Basic" + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            httpClient.addInterceptor()
        }
    }
    */
}
