package com.waiter.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseHistory {

    public class Data {

        @SerializedName("histories")
        @Expose
        private List<History> histories = null;

        public Data() {
        }

        public Data(List<History> histories) {
            super();
            this.histories = histories;
        }

        public List<History> getHistories() {
            return histories;
        }

        public void setHistories(List<History> histories) {
            this.histories = histories;
        }

    }

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private Data data;

    public ResponseHistory() {
    }

    public ResponseHistory(String status, Data data) {
        super();
        this.status = status;
        this.data = data;
    }

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