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
public class NearbyEnabledResponse implements Serializable {

    @Expose
    @SerializedName("enabled")
    private boolean enabled;

    @Expose
    @SerializedName("summary")
    private String summary;

    public NearbyEnabledResponse() {

    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
