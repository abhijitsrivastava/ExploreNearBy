/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

package com.coeverywhere.glass.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by ryaneldridge on 5/27/14.
 */
public class Pin implements Serializable {

    @Expose
    @SerializedName("id")
    private Long id;

    @Expose
    @SerializedName("avatar_url")
    private String avatarUrl;

    @Expose
    @SerializedName("type")
    private String type;

    @Expose
    @SerializedName("location")
    private CoordinatesMapping location;

    @Expose
    @SerializedName("title")
    private String title;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("date")
    private Date date;

    @Expose
    @SerializedName("created_at")
    private Date createdAt;

    @Expose
    @SerializedName("image_urls")
    private List<String> imageUrls;

    @Expose
    @SerializedName("twitter")
    private String twitter;

    @Expose
    @SerializedName("yelp")
    private String yelp;

    @Expose
    @SerializedName("faebook")
    private String facebook;

    @Expose
    @SerializedName("foursquare")
    private String foursquare;

    @Expose
    @SerializedName("phone")
    private String phone;

    @Expose
    @SerializedName("attached_image_url")
    private String attachedImageUrl;

    @Expose
    @SerializedName("address")
    private PinAddress pinAddress;

    public Pin() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CoordinatesMapping getLocation() {
        return location;
    }

    public void setLocation(CoordinatesMapping location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getYelp() {
        return yelp;
    }

    public void setYelp(String yelp) {
        this.yelp = yelp;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getFoursquare() {
        return foursquare;
    }

    public void setFoursquare(String foursquare) {
        this.foursquare = foursquare;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAttachedImageUrl() {
        return attachedImageUrl;
    }

    public void setAttachedImageUrl(String attachedImageUrl) {
        this.attachedImageUrl = attachedImageUrl;
    }

    public PinAddress getPinAddress() {
        return pinAddress;
    }

    public void setPinAddress(PinAddress pinAddress) {
        this.pinAddress = pinAddress;
    }
}
