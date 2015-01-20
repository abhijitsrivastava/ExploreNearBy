/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

package com.coeverywhere.glass.api;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by ryaneldridge on 5/21/14.
 */
public interface LogApi {

    @POST("/log")
    @FormUrlEncoded
    public void postLog(@Field("message") String message, Callback<Void> callback);

}
