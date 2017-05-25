package com.waiter.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Event implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("long")
    @Expose
    private Double _long;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("listOfWaiters")
    @Expose
    private List<String> listOfWaiters = null;

    public Event(String id, String name, String description, String address, Double _long, Double lat, String date, Integer v, List<String> listOfWaiters) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this._long = _long;
        this.lat = lat;
        this.date = date;
        this.v = v;
        this.listOfWaiters = listOfWaiters;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLong() {
        return _long;
    }

    public void setLong(Double _long) {
        this._long = _long;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public List<String> getListOfWaiters() {
        return listOfWaiters;
    }

    public void setListOfWaiters(List<String> listOfWaiters) {
        this.listOfWaiters = listOfWaiters;
    }

    /**
     * Constructs an Event from a Parcel
     * @param parcel Source Parcel
     */
    public Event(Parcel parcel) {
        this.id = parcel.readString();
        this.name = parcel.readString();
        this.description = parcel.readString();
        this.address = parcel.readString();
        this._long = parcel.readDouble();
        this.lat = parcel.readDouble();
        this.date = parcel.readString();
        parcel.readStringList(this.listOfWaiters);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(address);
        dest.writeDouble(_long);
        dest.writeDouble(lat);
        dest.writeString(date);
        dest.writeStringList(listOfWaiters);
    }

    // Method to recreate an Event from a Parcel
    public static Creator<Event> CREATOR = new Creator<Event>() {

        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }

    };

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}