package com.waiter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestValidateWait {

    @SerializedName("waiterId")
    @Expose
    private String waiterId;
    @SerializedName("code")
    @Expose
    private String code;

    /**
     * No args constructor for use in serialization
     *
     */
    public RequestValidateWait() {
    }

    /**
     *
     * @param code
     * @param waiterId
     */
    public RequestValidateWait(String waiterId, String code) {
        super();
        this.waiterId = waiterId;
        this.code = code;
    }

    public String getWaiterId() {
        return waiterId;
    }

    public void setWaiterId(String waiterId) {
        this.waiterId = waiterId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}