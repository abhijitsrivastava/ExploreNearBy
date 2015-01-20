/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

package com.coeverywhere.glass.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ryaneldridge on 5/1/14.
 */
public class Meta implements Serializable {

    @Expose
    @SerializedName("since")
    private Long since;

    @Expose
    @SerializedName("until")
    private Long until;

    @Expose
    @SerializedName("lng")
    private double longitude;

    @Expose
    @SerializedName("lat")
    private double latitude;

    @Expose
    @SerializedName("radius")
    private float radius;

    @Expose
    @SerializedName("area_description")
    private String areaDescription;

    @Expose
    @SerializedName("address")
    private String address;

    @Expose
    @SerializedName("max_id")
    private Long maxId;

    public Meta() {

    }

    public Long getSince() {
        return since;
    }

    public void setSince(Long since) {
        this.since = since;
    }

    public Long getUntil() {
        return until;
    }

    public void setUntil(Long until) {
        this.until = until;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getAreaDescription() {
        return areaDescription;
    }

    public void setAreaDescription(String areaDescription) {
        this.areaDescription = areaDescription;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }
}
