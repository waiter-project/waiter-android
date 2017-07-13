package com.waiter.models;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wait implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;
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
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("queueStart")
    @Expose
    private String queueStart;
    @SerializedName("queueEnd")
    @Expose
    private String queueEnd;
    @SerializedName("confirmationCode")
    @Expose
    private String confirmationCode;
    @SerializedName("nresponses")
    @Expose
    private List<Object> nresponses = null;
    @SerializedName("waitersIds")
    @Expose
    private List<String> waitersIds = null;
    public final static Parcelable.Creator<Wait> CREATOR = new Creator<Wait>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Wait createFromParcel(Parcel in) {
            Wait instance = new Wait();
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.state = ((String) in.readValue((String.class.getClassLoader())));
            instance.clientId = ((String) in.readValue((String.class.getClassLoader())));
            instance.eventId = ((String) in.readValue((String.class.getClassLoader())));
            instance.eventName = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.eventLocation, (java.lang.Double.class.getClassLoader()));
            instance.createdAt = ((String) in.readValue((String.class.getClassLoader())));
            instance.queueStart = ((String) in.readValue((String.class.getClassLoader())));
            instance.queueEnd = ((String) in.readValue((String.class.getClassLoader())));
            instance.confirmationCode = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.nresponses, (java.lang.Object.class.getClassLoader()));
            in.readList(instance.waitersIds, (java.lang.String.class.getClassLoader()));
            return instance;
        }

        public Wait[] newArray(int size) {
            return (new Wait[size]);
        }

    }
            ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public List<Double> getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(List<Double> eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getQueueStart() {
        return queueStart;
    }

    public void setQueueStart(String queueStart) {
        this.queueStart = queueStart;
    }

    public String getQueueEnd() {
        return queueEnd;
    }

    public void setQueueEnd(String queueEnd) {
        this.queueEnd = queueEnd;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public List<Object> getNresponses() {
        return nresponses;
    }

    public void setNresponses(List<Object> nresponses) {
        this.nresponses = nresponses;
    }

    public List<String> getWaitersIds() {
        return waitersIds;
    }

    public void setWaitersIds(List<String> waitersIds) {
        this.waitersIds = waitersIds;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(state);
        dest.writeValue(clientId);
        dest.writeValue(eventId);
        dest.writeValue(eventName);
        dest.writeList(eventLocation);
        dest.writeValue(createdAt);
        dest.writeValue(queueStart);
        dest.writeValue(queueEnd);
        dest.writeValue(confirmationCode);
        dest.writeList(nresponses);
        dest.writeList(waitersIds);
    }

    public int describeContents() {
        return 0;
    }

}