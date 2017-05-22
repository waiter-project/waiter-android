package com.waiter.network;

import com.waiter.models.RequestLogin;
import com.waiter.models.ResponseLogin;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface WaiterClient {
    @POST("/user/login")
    Call<ResponseLogin> login(@Body RequestLogin requestLogin);
}
