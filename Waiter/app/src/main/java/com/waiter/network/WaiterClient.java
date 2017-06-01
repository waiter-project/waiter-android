package com.waiter.network;

import com.waiter.models.RequestLogin;
import com.waiter.models.RequestSignup;
import com.waiter.models.ResponseLogin;
import com.waiter.models.ResponseSignup;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WaiterClient {
    @POST("/user/login")
    Call<ResponseLogin> login(@Body RequestLogin requestLogin);

    @POST("/user/register")
    Call<ResponseSignup> signup(@Body RequestSignup requestSignup);

    @GET("/user/available/{email}")
    Call<ResponseBody> checkEmailAvailable(@Path("email") String email);
}
