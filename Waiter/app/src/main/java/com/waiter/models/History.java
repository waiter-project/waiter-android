package com.waiter.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class History {

    public class Wait {
        @SerializedName("duration")
        @Expose
        private String duration;
        @SerializedName("price")
        @Expose
        private String price;

        /**
         * No args constructor for use in serialization
         *
         */
        public Wait() {
        }

        /**
         *
         * @param duration
         * @param price
         */
        public Wait(String duration, String price) {
            super();
            this.duration = duration;
            this.price = price;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }

    @SerializedName("wait")
    @Expose
    private Wait wait;
    @SerializedName("event")
    @Expose
    private Event event;
    @SerializedName("client")
    @Expose
    private User client;
    @SerializedName("waiters")
    @Expose
    private List<User> waiters = null;

    public History() {
    }

    public History(String waitDuration, String waitPrice, Event event, User client, List<User> waiters) {
        super();
        this.wait = new Wait(waitDuration, waitPrice);
        this.event = event;
        this.client = client;
        this.waiters = waiters;
    }

    public Wait getWait() {
        return wait;
    }

    public void setWait(Wait wait) {
        this.wait = wait;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public List<User> getWaiters() {
        return waiters;
    }

    public void setWaiters(List<User> waiters) {
        this.waiters = waiters;
    }

}