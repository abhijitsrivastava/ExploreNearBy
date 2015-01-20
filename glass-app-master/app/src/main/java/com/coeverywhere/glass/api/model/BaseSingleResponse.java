/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

package com.coeverywhere.glass.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ryaneldridge on 5/1/14.
 */
public class BaseSingleResponse<T> {

    @Expose
    @SerializedName("success")
    private boolean success;

    @Expose
    @SerializedName("error")
    private ResponseError error;

    @Expose
    @SerializedName("meta")
    private Meta meta;

    @Expose
    @SerializedName("result")
    private T result;

    public BaseSingleResponse() {

    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ResponseError getError() {
        return error;
    }

    public void setError(ResponseError error) {
        this.error = error;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
