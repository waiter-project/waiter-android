package com.waiter.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class History {

    public class Price {

        @SerializedName("total")
        @Expose
        private Double total;
        @SerializedName("pricebyHours")
        @Expose
        private List<Integer> pricebyHours = null;

        public Double getTotal() {
            return total;
        }

        public void setTotal(Double total) {
            this.total = total;
        }

        public List<Integer> getPricebyHours() {
            return pricebyHours;
        }

        public void setPricebyHours(List<Integer> pricebyHours) {
            this.pricebyHours = pricebyHours;
        }

    }

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("client")
    @Expose
    private User client;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("price")
    @Expose
    private Price price;
    @SerializedName("wait")
    @Expose
    private Wait wait;
    @SerializedName("waiters")
    @Expose
    private List<User> waiters = null;
    @SerializedName("event")
    @Expose
    private Event event;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Wait getWait() {
        return wait;
    }

    public void setWait(Wait wait) {
        this.wait = wait;
    }

    public List<User> getWaiters() {
        return waiters;
    }

    public void setWaiters(List<User> waiters) {
        this.waiters = waiters;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}