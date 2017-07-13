package com.waiter.network;

import com.waiter.models.RequestCreateWait;
import com.waiter.models.RequestLogin;
import com.waiter.models.RequestSignup;
import com.waiter.models.RequestUpdatePassword;
import com.waiter.models.RequestUpdateProfile;
import com.waiter.models.RequestValidateWait;
import com.waiter.models.ResponseEventsNearLocation;
import com.waiter.models.ResponseGenerateCode;
import com.waiter.models.ResponseLogin;
import com.waiter.models.ResponseSignup;
import com.waiter.models.ResponseWait;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface WaiterClient {

    // User Routes
    @POST("/user/login")
    Call<ResponseLogin> login(@Body RequestLogin requestLogin);

    @POST("/user/register")
    Call<ResponseSignup> signup(@Body RequestSignup requestSignup);

    @GET("/user/available/{email}")
    Call<ResponseBody> checkEmailAvailable(@Path("email") String email);

    @PUT("/user/{userId}/profile")
    Call<ResponseBody> updateProfile(@Header("x-access-token") String token, @Path("userId") String userId, @Body RequestUpdateProfile requestUpdateProfile);

    @PUT("/user/{userId}/password")
    Call<ResponseBody> updatePassword(@Header("x-access-token") String token, @Path("userId") String userId, @Body RequestUpdatePassword requestUpdatePassword);

    // Event Routes
    @GET("/event/long/{long}/lat/{lat}/zoom/{zoom}")
    Call<ResponseEventsNearLocation> getEventsNearLocation(@Path("long") double longitude, @Path("lat") double latitude, @Path("zoom") float zoom);

    @PUT("/event/{eventId}/join/{waiterId}")
    Call<ResponseBody> joinEvent(@Header("x-access-token") String token, @Path("eventId") String eventId, @Path("waiterId") String waiterId);

    @PUT("/event/{eventId}/leave/{waiterId}")
    Call<ResponseBody> leaveEvent(@Header("x-access-token") String token, @Path("eventId") String eventId, @Path("waiterId") String waiterId);

    // Wait Routes
    @POST("/wait")
    Call<ResponseWait> createWait(@Body RequestCreateWait requestCreateWait);

    @GET("/wait/user/{userId}")
    Call<ResponseWait> getCurrentWait(@Header("x-user-type") String userType, @Path("userId") String userId);

    @PUT("/wait/{waitId}/queue-start/{waiterId}")
    Call<ResponseWait> queueStart(@Path("waitId") String waitId, @Path("waiterId") String waiterId);

    @PUT("/wait/{waitId}/queue-done/{waiterId}")
    Call<ResponseWait> queueDone(@Path("waitId") String waitId, @Path("waiterId") String waiterId);

    @PUT("/wait/{waitId}/generate-code/{clientId}")
    Call<ResponseGenerateCode> generateCode(@Path("waitId") String waitId, @Path("clientId") String clientId);

    @PUT("/wait/{waitId}/validate")
    Call<ResponseWait> validateWait(@Path("waitId") String waitId, @Body RequestValidateWait requestValidateWait);
}
