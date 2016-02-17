package com.waiter.waiterpoc.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kharra_m on 17/02/2016.
 */
public class GenericResponseArray<T> {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<T> data = new ArrayList<T>();

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return
     * The data
     */
    public List<T> getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(List<T> data) {
        this.data = data;
    }
}
