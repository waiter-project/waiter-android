package com.waiter.waiterpoc.models;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class LoginResponse {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("tokenClient")
    @Expose
    private String tokenClient;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The tokenClient
     */
    public String getTokenClient() {
        return tokenClient;
    }

    /**
     *
     * @param tokenClient
     * The tokenClient
     */
    public void setTokenClient(String tokenClient) {
        this.tokenClient = tokenClient;
    }

}