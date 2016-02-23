package com.waiter.waiterpoc.network;

import com.waiter.waiterpoc.models.GenericResponseArray;
import com.waiter.waiterpoc.models.LoginAttempt;
import com.waiter.waiterpoc.models.RegisterAttempt;
import com.waiter.waiterpoc.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by kharra_m on 17/02/2016.
 */
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
    Call<LoginAttempt> basicLogin(@Body LoginAttempt loginAttempt);

    @Headers("app: client")
    @POST("/user/register")
    Call<RegisterAttempt> basicRegister(@Body RegisterAttempt registerAttempt);

    @GET("/user/list")
    Call<GenericResponseArray<User>> getUsersList();
}
