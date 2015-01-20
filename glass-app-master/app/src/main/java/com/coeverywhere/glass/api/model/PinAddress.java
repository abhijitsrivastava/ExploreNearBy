/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

package com.coeverywhere.glass.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ryaneldridge on 5/27/14.
 */
public class PinAddress implements Serializable {

    @Expose
    @SerializedName("line1")
    private String addressLine;

    @Expose
    @SerializedName("city")
    private String city;

    @Expose
    @SerializedName("state")
    private String state;

    @Expose
    @SerializedName("zip")
    private String zip;

    public PinAddress() {
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
