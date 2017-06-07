package com.waiter.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Event {

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
    @SerializedName("location")
    @Expose
    private List<Double> location = null;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("listOfWaiters")
    @Expose
    private List<String> listOfWaiters = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public Event() {
    }

    /**
     *
     * @param id
     * @param listOfWaiters
     * @param location
     * @param address
     * @param description
     * @param name
     * @param date
     */
    public Event(String id, String name, String description, String address, List<Double> location, String date, List<String> listOfWaiters) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.location = location;
        this.date = date;
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

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getListOfWaiters() {
        return listOfWaiters;
    }

    public void setListOfWaiters(List<String> listOfWaiters) {
        this.listOfWaiters = listOfWaiters;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", location=" + location +
                ", date='" + date + '\'' +
                ", listOfWaiters=" + listOfWaiters +
                '}';
    }
}