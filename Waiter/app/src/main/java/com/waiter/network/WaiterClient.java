package com.waiter.network;

import com.waiter.models.RequestLogin;
import com.waiter.models.RequestSignup;
import com.waiter.models.RequestUpdateProfile;
import com.waiter.models.ResponseLogin;
import com.waiter.models.ResponseSignup;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface WaiterClient {
    @POST("/user/login")
    Call<ResponseLogin> login(@Body RequestLogin requestLogin);

    @POST("/user/register")
    Call<ResponseSignup> signup(@Body RequestSignup requestSignup);

    @GET("/user/available/{email}")
    Call<ResponseBody> checkEmailAvailable(@Path("email") String email);

    @PUT("/user/{userId}/profile")
    Call<ResponseBody> updateProfile(@Header("x-access-token") String token, @Path("userId") String userId, @Body RequestUpdateProfile requestUpdateProfile);
}
