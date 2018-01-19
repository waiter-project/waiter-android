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

    public class Notation {

        @SerializedName("notation")
        @Expose
        private Integer notation;
        @SerializedName("date")
        @Expose
        private String date;

        /**
         * No args constructor for use in serialization
         *
         */
        public Notation() {
        }

        /**
         *
         * @param date
         * @param notation
         */
        public Notation(Integer notation, String date) {
            super();
            this.notation = notation;
            this.date = date;
        }

        public Integer getNotation() {
            return notation;
        }

        public void setNotation(Integer notation) {
            this.notation = notation;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

    }

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("client")
    @Expose
    private User client;
    @SerializedName("notation")
    @Expose
    private Notation notation;
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

    /**
     * No args constructor for use in serialization
     *
     */
    public History() {
    }

    /**
     *
     * @param id
     * @param waiters
     * @param price
     * @param client
     * @param event
     * @param wait
     * @param notation
     */
    public History(String id, User client, Notation notation, Price price, Wait wait, List<User> waiters, Event event) {
        super();
        this.id = id;
        this.client = client;
        this.notation = notation;
        this.price = price;
        this.wait = wait;
        this.waiters = waiters;
        this.event = event;
    }

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

    public Notation getNotation() {
        return notation;
    }

    public void setNotation(Notation notation) {
        this.notation = notation;
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