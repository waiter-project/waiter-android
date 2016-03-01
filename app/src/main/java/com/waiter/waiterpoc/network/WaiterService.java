package com.waiter.waiterpoc.network;

import com.waiter.waiterpoc.models.Event;
import com.waiter.waiterpoc.models.GenericResponse;
import com.waiter.waiterpoc.models.GenericResponseArray;
import com.waiter.waiterpoc.models.LoginAttempt;
import com.waiter.waiterpoc.models.LoginResponse;
import com.waiter.waiterpoc.models.RegisterAttempt;
import com.waiter.waiterpoc.models.RegisterResponse;
import com.waiter.waiterpoc.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WaiterService {
    //You can use rx.java for sophisticated composition of requests
    //@GET("/users/{user}")
    //public Observable<UserModel> fetchUser(@Path("user") String user);

    //or you can just get your model if you use json api
    //@GET("/users/{user}")
    //public UserModel fetchUser(@Path("user") String user);

    //or if there are some special cases you can process your response manually
    //@GET("/users/{user}")
    //public Response fetchUser(@Path("user") String user);

    //@GET("/users/list")
    //public void getFeed(Callback<List<UserModel>>  response);

    @Headers("app: client")
    @POST("/user/login")
    Call<GenericResponse<LoginResponse>> basicLogin(@Body LoginAttempt loginAttempt);

    @GET("user/read/{userId}")
    Call<GenericResponse<User>> getUser(@Path("userId") String userId);

    @Headers("app: client")
    @POST("/user/register")
    Call<GenericResponse<RegisterResponse>> basicRegister(@Body RegisterAttempt registerAttempt);

    @GET("/event/list")
    Call<GenericResponseArray<Event>> getEventsList();

    @GET("/event/read/{eventId}")
    Call<GenericResponse<Event>> getEvent(@Path("eventId") String eventId);

    @GET("/user/list")
    Call<GenericResponseArray<User>> getUsersList();
}
