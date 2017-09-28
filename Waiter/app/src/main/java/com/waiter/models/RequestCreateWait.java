package com.waiter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestCreateWait {

    @SerializedName("eventId")
    @Expose
    private String eventId;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("numberOfWaiters")
    @Expose
    private int numberOfWaiters;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getNumberOfWaiters() {
        return numberOfWaiters;
    }

    public void setNumberOfWaiters(int numberOfWaiters) {
        this.numberOfWaiters = numberOfWaiters;
    }

}