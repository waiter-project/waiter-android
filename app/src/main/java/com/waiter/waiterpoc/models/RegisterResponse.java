package com.waiter.waiterpoc.models;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class RegisterResponse {

    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("tokenClient")
    @Expose
    private String tokenClient;
    @SerializedName("wallet")
    @Expose
    private Integer wallet;
    @SerializedName("waiterMark")
    @Expose
    private Integer waiterMark;
    @SerializedName("nbMark")
    @Expose
    private Integer nbMark;
    @SerializedName("nbWaitDone")
    @Expose
    private Integer nbWaitDone;
    @SerializedName("nbWaitDenied")
    @Expose
    private Integer nbWaitDenied;
    @SerializedName("engaged")
    @Expose
    private Boolean engaged;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("id")
    @Expose
    private String id;

    /**
     *
     * @return
     * The firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     *
     * @param firstname
     * The firstname
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     *
     * @return
     * The lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     *
     * @param lastname
     * The lastname
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
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

    /**
     *
     * @return
     * The wallet
     */
    public Integer getWallet() {
        return wallet;
    }

    /**
     *
     * @param wallet
     * The wallet
     */
    public void setWallet(Integer wallet) {
        this.wallet = wallet;
    }

    /**
     *
     * @return
     * The waiterMark
     */
    public Integer getWaiterMark() {
        return waiterMark;
    }

    /**
     *
     * @param waiterMark
     * The waiterMark
     */
    public void setWaiterMark(Integer waiterMark) {
        this.waiterMark = waiterMark;
    }

    /**
     *
     * @return
     * The nbMark
     */
    public Integer getNbMark() {
        return nbMark;
    }

    /**
     *
     * @param nbMark
     * The nbMark
     */
    public void setNbMark(Integer nbMark) {
        this.nbMark = nbMark;
    }

    /**
     *
     * @return
     * The nbWaitDone
     */
    public Integer getNbWaitDone() {
        return nbWaitDone;
    }

    /**
     *
     * @param nbWaitDone
     * The nbWaitDone
     */
    public void setNbWaitDone(Integer nbWaitDone) {
        this.nbWaitDone = nbWaitDone;
    }

    /**
     *
     * @return
     * The nbWaitDenied
     */
    public Integer getNbWaitDenied() {
        return nbWaitDenied;
    }

    /**
     *
     * @param nbWaitDenied
     * The nbWaitDenied
     */
    public void setNbWaitDenied(Integer nbWaitDenied) {
        this.nbWaitDenied = nbWaitDenied;
    }

    /**
     *
     * @return
     * The engaged
     */
    public Boolean getEngaged() {
        return engaged;
    }

    /**
     *
     * @param engaged
     * The engaged
     */
    public void setEngaged(Boolean engaged) {
        this.engaged = engaged;
    }

    /**
     *
     * @return
     * The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     * The createdAt
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     *
     * @return
     * The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     *
     * @param updatedAt
     * The updatedAt
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

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

}