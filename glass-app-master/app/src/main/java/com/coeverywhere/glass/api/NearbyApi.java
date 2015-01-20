/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

package com.coeverywhere.glass.api;

import com.coeverywhere.glass.api.model.BaseMultipleResponse;
import com.coeverywhere.glass.api.model.BaseSingleResponse;
import com.coeverywhere.glass.api.model.NearbyEnabledResponse;
import com.coeverywhere.glass.api.model.StoriesModel;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by ryaneldridge on 5/1/14.
 */
public interface NearbyApi {

    @GET("/nearby/enabled")
    public void enableNearby(@Query("lng") String longitude, @Query("lat") String latitude, Callback<BaseSingleResponse<NearbyEnabledResponse>> callback);

    @GET("/nearby/bootstrap")
    public void bootstrapNearby(@Query("lng") String longitude, @Query("lat") String latitude, Callback<BaseMultipleResponse<StoriesModel>> callback);

    @GET("/nearby/bootstrap")
    public BaseMultipleResponse<StoriesModel> bootstrapNearby(@Query("lng") String longitude, @Query("lat") String latitude);

    @GET("/nearby/stories/{feed}")
    public void getFeed(@Path("feed") String feed, @Query("lng") String longitude, @Query("lat") String latitude, @Query("radius") String radius,
                        Callback<BaseMultipleResponse<StoriesModel>> callback);

    @GET("/nearby/stories/{feed}")
    public void getFeed(@Path("feed") String feed, @Query("lng") String longitude, @Query("lat") String latitude, @Query("radius") String radius,
                        @Query("until") String until, Callback<BaseMultipleResponse<StoriesModel>> callback);

    @GET("/nearby/stories/{feed}")
    public void pageFeed(@Path("feed") String feed, @Query("lng") String longitude, @Query("lat") String latitude, @Query("radius") String radius,
                         @Query("until") String since, Callback<BaseMultipleResponse<StoriesModel>> callback);

    @GET("/nearby/stories/{feed}")
    public BaseMultipleResponse<StoriesModel> getFeed(@Path("feed") String feed, @Query("lng") String longitude, @Query("lat") String latitude, @Query("radius") String radius);

    @GET("/nearby/stories/{feed}")
    public BaseMultipleResponse<StoriesModel> getFeed(@Path("feed") String feed, @Query("lng") String longitude, @Query("lat") String latitude, @Query("radius") String radius, @Query("until") String since);

    @GET("/nearby/stories/{feed}")
    public BaseMultipleResponse<StoriesModel> pageFeed(@Path("feed") String feed, @Query("lng") String longitude, @Query("lat") String latitude, @Query("radius") String radius,
                                                       @Query("until") String since);

}
