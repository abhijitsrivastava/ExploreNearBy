/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

package com.coeverywhere.glass.adapter.card;

import android.content.Context;

import com.coeverywhere.glass.api.model.StoriesModel;
import com.coeverywhere.glass.api.model.StoryUser;
import com.coeverywhere.glass.util.MathUtils;
import com.google.android.glass.app.Card;

/**
 * Created by ryaneldridge on 5/1/14.
 */
public class CoCard extends Card {

    private Context context;
    private String storyId;
    private String url;
    private String distance;
    private Long createdAt;
    private StoryUser user;
    private String sourceLogo;
    private StoriesModel model;
    private Float rawDistance;

    private float bearing;
    private MathUtils.CompassDirection compassDirection;
    private int height;
    private int paintColor;

    public CoCard(Context context, String url, String storyId) {
        super(context);
        this.context = context;
        this.url = url;
        this.storyId = storyId;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public StoryUser getUser() {
        return user;
    }

    public void setUser(StoryUser user) {
        this.user = user;
    }

    public String getSourceLogo() {
        return sourceLogo;
    }

    public void setSourceLogo(String sourceLogo) {
        this.sourceLogo = sourceLogo;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public MathUtils.CompassDirection getCompassDirection() {
        return compassDirection;
    }

    public void setCompassDirection(MathUtils.CompassDirection compassDirection) {
        this.compassDirection = compassDirection;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }

    public StoriesModel getModel() {
        return model;
    }

    public void setModel(StoriesModel model) {
        this.model = model;
    }

    public Float getRawDistance() {
        return rawDistance;
    }

    public void setRawDistance(Float rawDistance) {
        this.rawDistance = rawDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoCard coCard = (CoCard) o;

        if (storyId != null ? !storyId.equals(coCard.storyId) : coCard.storyId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return storyId != null ? storyId.hashCode() : 0;
    }
}
