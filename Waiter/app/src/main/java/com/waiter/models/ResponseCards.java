package com.waiter.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseCards {

    public class Data {

        @SerializedName("cards")
        @Expose
        private List<String> cards = null;

        public List<String> getCards() {
            return cards;
        }

        public void setCards(List<String> cards) {
            this.cards = cards;
        }

    }

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}