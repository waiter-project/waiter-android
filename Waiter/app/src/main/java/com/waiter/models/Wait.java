package com.waiter.models;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wait implements Parcelable {

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
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("nresponses")
    @Expose
    private List<Object> nresponses = null;
    @SerializedName("eventLocation")
    @Expose
    private List<Double> eventLocation = null;
    @SerializedName("waitersIds")
    @Expose
    private List<String> waitersIds = null;
    public final static Parcelable.Creator<Wait> CREATOR = new Creator<Wait>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Wait createFromParcel(Parcel in) {
            Wait instance = new Wait();
            instance.state = ((String) in.readValue((String.class.getClassLoader())));
            instance.clientId = ((String) in.readValue((String.class.getClassLoader())));
            instance.eventId = ((String) in.readValue((String.class.getClassLoader())));
            instance.eventName = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.createdAt = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.nresponses, (java.lang.Object.class.getClassLoader()));
            in.readList(instance.eventLocation, (java.lang.Double.class.getClassLoader()));
            in.readList(instance.waitersIds, (java.lang.String.class.getClassLoader()));
            return instance;
        }

        public Wait[] newArray(int size) {
            return (new Wait[size]);
        }

    }
            ;

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

    public List<Object> getNresponses() {
        return nresponses;
    }

    public void setNresponses(List<Object> nresponses) {
        this.nresponses = nresponses;
    }

    public List<Double> getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(List<Double> eventLocation) {
        this.eventLocation = eventLocation;
    }

    public List<String> getWaitersIds() {
        return waitersIds;
    }

    public void setWaitersIds(List<String> waitersIds) {
        this.waitersIds = waitersIds;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(state);
        dest.writeValue(clientId);
        dest.writeValue(eventId);
        dest.writeValue(eventName);
        dest.writeValue(id);
        dest.writeValue(createdAt);
        dest.writeList(nresponses);
        dest.writeList(eventLocation);
        dest.writeList(waitersIds);
    }

    public int describeContents() {
        return 0;
    }

}