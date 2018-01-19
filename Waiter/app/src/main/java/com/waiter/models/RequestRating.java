package com.waiter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestRating {

    @SerializedName("notation")
    @Expose
    private Integer notation;

    /**
     * No args constructor for use in serialization
     *
     */
    public RequestRating() {
    }

    /**
     *
     * @param notation
     */
    public RequestRating(Integer notation) {
        super();
        this.notation = notation;
    }

    public Integer getNotation() {
        return notation;
    }

    public void setNotation(Integer notation) {
        this.notation = notation;
    }

}