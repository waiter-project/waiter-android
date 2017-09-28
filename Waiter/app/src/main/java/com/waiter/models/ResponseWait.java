package com.waiter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseWait {

    public class Data {

        @SerializedName("wait")
        @Expose
        private Wait wait;

        public Wait getWait() {
            return wait;
        }

        public void setWait(Wait wait) {
            this.wait = wait;
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