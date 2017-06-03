
package com.waiter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestUpdatePassword {

    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("newPassword")
    @Expose
    private String newPassword;

    public RequestUpdatePassword() {
    }

    public RequestUpdatePassword(String password, String newPassword) {
        super();
        this.password = password;
        this.newPassword = newPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}