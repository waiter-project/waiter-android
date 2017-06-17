package com.waiter.network;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.waiter.MainActivity;
import com.waiter.SettingsActivity;
import com.waiter.Utils;
import com.waiter.WaiterApplication;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static String BASE_URL = setApiBaseUrl();

    private static String setApiBaseUrl() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(WaiterApplication.getInstance().getApplicationContext());
        boolean customApiPref = sharedPref.getBoolean(SettingsActivity.KEY_PREF_CUSTOM_API, true);
        if (!customApiPref) {
            String serverPref = sharedPref.getString(SettingsActivity.KEY_PREF_SERVER, "0");
            if (serverPref.equals("0")) {
                return "http://10.0.2.2:5000";
            } else {
                return "http://wait4me.tk:5000";
            }
        } else {
            String apiUrlPref = sharedPref.getString(SettingsActivity.KEY_PREF_CUSTOM_API_URL, "http://wait4me.tk:5000");
            return apiUrlPref;
        }
    }

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    public static Retrofit retrofit() {
        return retrofit;
    }

    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY);

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    public static <S> S createService(
            Class<S> serviceClass) {
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }
}

//package com.waiter.network;
//
//import com.waiter.Utils;
//
//import okhttp3.OkHttpClient;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class ServiceGenerator {
//
//    private static final String API_BASE_URL = setApiBaseUrl();
//    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//
//    private static String setApiBaseUrl() {
//        if (Utils.isEmulator()) {
//            return "http://10.0.2.2:5000";
//        }
//        return "http://192.168.1.9:5000";
//    }
//
//    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(API_BASE_URL).addConverterFactory(GsonConverterFactory.create());
//
//    private static Retrofit retrofit = builder.client(httpClient.build()).build();
//
//    public static Retrofit retrofit() {
//        return retrofit;
//    }
//
//    public static <S> S createService(Class<S> clientClass) {
//        /*
//         ** Debug log
//         */
//        /*
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        httpClient.addInterceptor(interceptor);
//        */
//
//        Retrofit retrofit = builder.client(httpClient.build()).build();
//
//        return retrofit.create(clientClass);
//    }
//
//    /*
//    public static <S> S createService(Class<S> clientClass, String email, String password) {
//        if (email != null && password != null) {
//            String credentials = email + ":" + password;
//            final String basic = "Basic" + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//            httpClient.addInterceptor()
//        }
//    }
//    */
//}
