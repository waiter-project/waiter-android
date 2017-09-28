package com.waiter.utils;

import android.util.Log;

import com.waiter.models.ErrorResponse;
import com.waiter.network.ServiceGenerator;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtils {

    private static final String TAG = "ErrorUtils";

    public static ErrorResponse parseError(Response<?> response) {
        Converter<ResponseBody, ErrorResponse> converter =
                ServiceGenerator.retrofit().responseBodyConverter(ErrorResponse.class, new Annotation[0]);

        ErrorResponse error = new ErrorResponse();

        try {
            ResponseBody errorBody = response.errorBody();
            if (errorBody != null) {
                error = converter.convert(errorBody);
            } else {
                Log.d(TAG, "parseError: errorBody is null");
            }
        } catch (IOException e) {
            return new ErrorResponse();
        }

        return error;
    }
}
