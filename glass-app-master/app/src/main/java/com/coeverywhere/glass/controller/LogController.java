/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

package com.coeverywhere.glass.controller;

import android.content.Context;

import com.coeverywhere.glass.R;
import com.coeverywhere.glass.api.LogApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.Callback;
import retrofit.Endpoint;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by ryaneldridge on 5/21/14.
 */
public class LogController {

    private static LogController instance;

    private Context context;
    private LogApi mApi;

    private static final Object LOCK = new Object();

    private LogController(Context context) {
        this.context = context;
        mApi = buildRestAdapter().create(LogApi.class);
    }

    public static LogController getInstance(Context context) {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new LogController(context);
            }
        }
        return instance;
    }

    public void postLogMessage(String message) {
        mApi.postLog(message, new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private RestAdapter buildRestAdapter() {
        return new RestAdapter.Builder()
                .setClient(new OkClient())
                .setConverter(new GsonConverter(configureGson()))
                .setEndpoint(new Endpoint() {
                    @Override
                    public String getUrl() {
                        return getResource(R.string.log_base_url);
                    }

                    @Override
                    public String getName() {
                        return "SERVER";
                    }
                }).build();
    }


    private Gson configureGson() {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    private String getResource(int resourceId) {
        return context.getResources().getString(resourceId);
    }

}
