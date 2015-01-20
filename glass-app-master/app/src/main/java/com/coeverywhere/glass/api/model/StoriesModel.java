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
public class StoriesModel implements Serializable {

    @Expose
    @SerializedName("id")
    private String storyId;

    @Expose
    @SerializedName("created_at")
    private Long createdAt;

    @Expose
    @SerializedName("source")
    private String source;

    @Expose
    @SerializedName("source_id")
    private String sourceId;

    @Expose
    @SerializedName("source_logo_url")
    private String sourceLogoUrl;

    @Expose
    @SerializedName("source_url")
    private String sourceUrl;

    @Expose
    @SerializedName("title")
    private String title;

    @Expose
    @SerializedName("content")
    private String content;

    @Expose
    @SerializedName("attached_image_url")
    private String attachedImageUrl;

    @Expose
    @SerializedName("attached_image_width")
    private int attachedImageWidth;

    @Expose
    @SerializedName("attached_image_height")
    private int attachedImageHeight;

    @Expose
    @SerializedName("attached_thumb_url")
    private String attachedImageThumb;

    @Expose
    @SerializedName("attached_thumb_width")
    private int attachedThumbWidth;

    @Expose
    @SerializedName("attached_thumb_height")
    private int attachedThumbHeight;

    @Expose
    @SerializedName("attached_video_url")
    private String attachedVideoUrl;

    @Expose
    @SerializedName("attached_url")
    private String attachedUrl;

    @Expose
    @SerializedName("attached_url_title")
    private String attachedUrlTitle;

    @Expose
    @SerializedName("attached_url_caption")
    private String attachedUrlCaption;

    @Expose
    @SerializedName("attached_url_image")
    private String attachedUrlImage;

    @Expose
    @SerializedName("mute_key")
    private String muteKey;

    @Expose
    @SerializedName("mute_label")
    private String muteLabel;

    @Expose
    @SerializedName("location")
    private CoordinatesMapping location;

    @Expose
    @SerializedName("rating")
    private double rating;

    @Expose
    @SerializedName("rating_image_url")
    private String ratingImageUrl;

    @Expose
    @SerializedName("price")
    private double price;

    @Expose
    @SerializedName("price_formatted")
    private String priceFormatted;

    @Expose
    @SerializedName("original_price")
    private double originalPrice;

    @Expose
    @SerializedName("original_price_formatted")
    private String originalPriceFormatted;

    @Expose
    @SerializedName("discount_percent")
    private double discountPercent;

    @Expose
    @SerializedName("discount_percent_formatted")
    private String discountPercentFormatted;

    @Expose
    @SerializedName("start_date")
    private Long startDate;

    @Expose
    @SerializedName("end_date")
    private Long endDate;

    @Expose
    @SerializedName("pin")
    private Pin pin;

    @Expose
    @SerializedName("share_url")
    private String shareUrl;

    @Expose
    @SerializedName("user_avatar")
    private String userAvatar;

    @Expose
    @SerializedName("user_id")
    private long userId;

    @Expose
    @SerializedName("user_name")
    private String userName;

    @Expose
    @SerializedName("user_url")
    private String userUrl;

    @Expose
    @SerializedName("user")
    private StoryUser user;

    private float bearing;

    public StoriesModel() {

    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceLogoUrl() {
        return sourceLogoUrl;
    }

    public void setSourceLogoUrl(String sourceLogoUrl) {
        this.sourceLogoUrl = sourceLogoUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachedImageUrl() {
        return attachedImageUrl;
    }

    public void setAttachedImageUrl(String attachedImageUrl) {
        this.attachedImageUrl = attachedImageUrl;
    }

    public int getAttachedImageWidth() {
        return attachedImageWidth;
    }

    public void setAttachedImageWidth(int attachedImageWidth) {
        this.attachedImageWidth = attachedImageWidth;
    }

    public int getAttachedImageHeight() {
        return attachedImageHeight;
    }

    public void setAttachedImageHeight(int attachedImageHeight) {
        this.attachedImageHeight = attachedImageHeight;
    }

    public String getAttachedImageThumb() {
        return attachedImageThumb;
    }

    public void setAttachedImageThumb(String attachedImageThumb) {
        this.attachedImageThumb = attachedImageThumb;
    }

    public int getAttachedThumbWidth() {
        return attachedThumbWidth;
    }

    public void setAttachedThumbWidth(int attachedThumbWidth) {
        this.attachedThumbWidth = attachedThumbWidth;
    }

    public int getAttachedThumbHeight() {
        return attachedThumbHeight;
    }

    public void setAttachedThumbHeight(int attachedThumbHeight) {
        this.attachedThumbHeight = attachedThumbHeight;
    }

    public String getAttachedVideoUrl() {
        return attachedVideoUrl;
    }

    public void setAttachedVideoUrl(String attachedVideoUrl) {
        this.attachedVideoUrl = attachedVideoUrl;
    }

    public String getAttachedUrl() {
        return attachedUrl;
    }

    public void setAttachedUrl(String attachedUrl) {
        this.attachedUrl = attachedUrl;
    }

    public String getAttachedUrlTitle() {
        return attachedUrlTitle;
    }

    public void setAttachedUrlTitle(String attachedUrlTitle) {
        this.attachedUrlTitle = attachedUrlTitle;
    }

    public String getAttachedUrlCaption() {
        return attachedUrlCaption;
    }

    public void setAttachedUrlCaption(String attachedUrlCaption) {
        this.attachedUrlCaption = attachedUrlCaption;
    }

    public String getAttachedUrlImage() {
        return attachedUrlImage;
    }

    public void setAttachedUrlImage(String attachedUrlImage) {
        this.attachedUrlImage = attachedUrlImage;
    }

    public String getMuteKey() {
        return muteKey;
    }

    public void setMuteKey(String muteKey) {
        this.muteKey = muteKey;
    }

    public String getMuteLabel() {
        return muteLabel;
    }

    public void setMuteLabel(String muteLabel) {
        this.muteLabel = muteLabel;
    }

    public CoordinatesMapping getLocation() {
        return location;
    }

    public void setLocation(CoordinatesMapping location) {
        this.location = location;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getRatingImageUrl() {
        return ratingImageUrl;
    }

    public void setRatingImageUrl(String ratingImageUrl) {
        this.ratingImageUrl = ratingImageUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPriceFormatted() {
        return priceFormatted;
    }

    public void setPriceFormatted(String priceFormatted) {
        this.priceFormatted = priceFormatted;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getOriginalPriceFormatted() {
        return originalPriceFormatted;
    }

    public void setOriginalPriceFormatted(String originalPriceFormatted) {
        this.originalPriceFormatted = originalPriceFormatted;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getDiscountPercentFormatted() {
        return discountPercentFormatted;
    }

    public void setDiscountPercentFormatted(String discountPercentFormatted) {
        this.discountPercentFormatted = discountPercentFormatted;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Pin getPin() {
        return pin;
    }

    public void setPin(Pin pin) {
        this.pin = pin;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public StoryUser getUser() {
        return user;
    }

    public void setUser(StoryUser user) {
        this.user = user;
    }

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StoriesModel that = (StoriesModel) o;

        if (storyId != null ? !storyId.equals(that.storyId) : that.storyId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return storyId != null ? storyId.hashCode() : 0;
    }*/
}
