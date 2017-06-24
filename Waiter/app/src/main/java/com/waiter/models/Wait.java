package com.waiter.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wait {

    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("clientId")
    @Expose
    private String clientId;
    @SerializedName("eventId")
    @Expose
    private String eventId;
    @SerializedName("eventName")
    @Expose
    private String eventName;
    @SerializedName("eventLocation")
    @Expose
    private List<Double> eventLocation = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("nresponses")
    @Expose
    private List<String> nresponses = null;
    @SerializedName("waitersIds")
    @Expose
    private List<String> waitersIds = null;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public List<Double> getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(List<Double> eventLocation) {
        this.eventLocation = eventLocation;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getNresponses() {
        return nresponses;
    }

    public void setNresponses(List<String> nresponses) {
        this.nresponses = nresponses;
    }

    public List<String> getWaitersIds() {
        return waitersIds;
    }

    public void setWaitersIds(List<String> waitersIds) {
        this.waitersIds = waitersIds;
    }

}