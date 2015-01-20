/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

package com.coeverywhere.glass.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ryaneldridge on 5/19/14.
 */
public class StoryUser implements Serializable {

    @Expose
    @SerializedName("avatar_url")
    private String avatarUrl;

    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("logo_url")
    private String logoUrl;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("source")
    private String source;

    @Expose
    @SerializedName("url")
    private String url;

    public StoryUser() {

    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
