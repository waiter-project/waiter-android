package com.waiter.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ErrorResponse {

    public class Data {

        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("causes")
        @Expose
        private List<String> causes = null;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<String> getCauses() {
            return causes;
        }

        public void setCauses(List<String> causes) {
            this.causes = causes;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}