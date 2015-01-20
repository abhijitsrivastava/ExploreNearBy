/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

package com.coeverywhere.glass.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.coeverywhere.glass.R;
import com.coeverywhere.glass.api.NearbyApi;
import com.coeverywhere.glass.api.model.BaseMultipleResponse;
import com.coeverywhere.glass.api.model.BaseSingleResponse;
import com.coeverywhere.glass.api.model.NearbyEnabledResponse;
import com.coeverywhere.glass.api.model.StoriesModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit.Callback;
import retrofit.Endpoint;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import timber.log.Timber;

/**
 * Created by ryaneldridge on 5/1/14.
 */
public class NearbyController {

    private static final Object LOCK = new Object();

    private static NearbyController instance;

    private Context context;
    private Handler mHandler = null;
    private NearbyApi mApi;


    private NearbyController(Context context) {
        this.context = context;
        mHandler = new Handler(Looper.getMainLooper());
        mApi = buildRestAdapter().create(NearbyApi.class);
    }

    public static NearbyController getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new NearbyController(context);
            }
        }
        return instance;
    }

    public void enableNearby(String lng, String lat, Callback<BaseSingleResponse<NearbyEnabledResponse>> callback) {
        mApi.enableNearby(lng, lat, callback);
    }

    public void bootstrapNearby(final String lng, final String lat, final Callback<BaseMultipleResponse<StoriesModel>> callback) {
        mApi.bootstrapNearby(lng, lat, callback);
        /*final BaseMultipleResponse<StoriesModel> storiesModelBaseMultipleResponse = new BaseMultipleResponse<StoriesModel>();
        final List<StoriesModel> stories = new ArrayList<StoriesModel>();
        new Thread("feed") {
            @Override
            public void run() {
                try {
                    Set<StoriesModel> set = new HashSet<StoriesModel>();
                    BaseMultipleResponse<StoriesModel> bootstrap = mApi.bootstrapNearby(lng, lat);
                    if (bootstrap != null && bootstrap.isSuccess() && bootstrap.getResult() != null && !bootstrap.getResult().isEmpty()) {
                        set.addAll(bootstrap.getResult());
                        if (bootstrap.getMeta() != null && bootstrap.getMeta().getSince() != null) {
                            recursivePageFeed(lng, lat, String.valueOf(bootstrap.getMeta().getRadius()),
                                    String.valueOf(bootstrap.getMeta().getSince()), set);
                            stories.addAll(set);
                        }
                    }
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            storiesModelBaseMultipleResponse.setSuccess(true);
                            storiesModelBaseMultipleResponse.setResult(stories);
                            callback.success(storiesModelBaseMultipleResponse, null);
                        }
                    });
                } catch (final RetrofitError re) {
                    Timber.e(re, "Error getting feed");
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.failure(re);
                        }
                    });
                }
            }
        }.start();*/
    }

    public void getNearbyStories(final String lng, final String lat, final String radius, final String until, final Callback<BaseMultipleResponse<StoriesModel>> callback) {
        //mApi.getFeed("media", lng, lat, radius, callback);
        final BaseMultipleResponse<StoriesModel> storiesModelBaseMultipleResponse = new BaseMultipleResponse<StoriesModel>();
        final List<StoriesModel> stories = new ArrayList<StoriesModel>();
        new Thread("feed") {
            @Override
            public void run() {
                try {
                    Set<StoriesModel> set = new HashSet<StoriesModel>();
                    BaseMultipleResponse<StoriesModel> bootstrap = null;
                    if (until != null) {
                        bootstrap = mApi.getFeed("media", lng, lat, radius, until);
                    } else {
                        bootstrap = mApi.getFeed("media", lng, lat, radius);
                    }
                    //BaseMultipleResponse<StoriesModel> bootstrap = mApi.getFeed("media", lng, lat, radius);
                    if (bootstrap != null && bootstrap.isSuccess() && bootstrap.getResult() != null && !bootstrap.getResult().isEmpty()) {
                        set.addAll(bootstrap.getResult());
                        if (bootstrap.getMeta() != null && bootstrap.getMeta().getSince() != null) {
                            //recursivePageFeed(lng, lat, radius, String.valueOf(bootstrap.getMeta().getSince()), set);
                            BaseMultipleResponse<StoriesModel> page = pageFeed(lng, lat, radius, String.valueOf(bootstrap.getMeta().getSince()));
                            if (page != null && page.isSuccess() && page.getResult() != null && !page.getResult().isEmpty()) {
                                set.addAll(page.getResult());
                            }
                            stories.addAll(set);
                        } else {
                            stories.addAll(set);
                        }
                    }
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            storiesModelBaseMultipleResponse.setSuccess(true);
                            storiesModelBaseMultipleResponse.setResult(stories);
                            callback.success(storiesModelBaseMultipleResponse, null);
                        }
                    });
                } catch (final RetrofitError re) {
                    Timber.e(re, "Error getting feed");
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.failure(re);
                        }
                    });
                }
            }
        }.start();
    }

    public void pageFeed(final String lng, final String lat, final String radius, final String since, final Callback<BaseMultipleResponse<StoriesModel>> callback) {
        new Thread("paging-thread") {
            @Override
            public void run() {
                mApi.pageFeed("media", lng, lat, radius, since, callback);
            }
        }.start();
    }

    private BaseMultipleResponse<StoriesModel> getFeed(final String lng, final String lat, final String radius) {
        return mApi.getFeed("media", lng, lat, radius);
    }

    private BaseMultipleResponse<StoriesModel> pageFeed(final String lng, final String lat, final String radius, final String since) {
        return mApi.pageFeed("media", lng, lat, radius, since);
    }

    private void recursivePageFeed(final String lng, final String lat, final String radius, final String since, Set<StoriesModel> stories) {
        Timber.d("*** Fetching stories: ***");

        BaseMultipleResponse<StoriesModel> baseMultipleResponse = pageFeed(lng, lat, radius, since);
        if (baseMultipleResponse != null && baseMultipleResponse.isSuccess() && baseMultipleResponse.getResult() != null
                && !baseMultipleResponse.getResult().isEmpty()) {
            for (StoriesModel story : baseMultipleResponse.getResult()) {
                if (!stories.contains(story)) {
                    stories.add(story);
                }
            }
            if (baseMultipleResponse.getMeta() != null && baseMultipleResponse.getMeta().getSince() != null) {
                Date originalSince = new Date(new Long(since) * 1000);
                Date currentSince = new Date(baseMultipleResponse.getMeta().getSince() * 1000);
                if (currentSince.compareTo(originalSince) < 0) {
                    recursivePageFeed(lng, lat, radius, String.valueOf(baseMultipleResponse.getMeta().getSince()), stories);
                }
            }
        }
    }

    private void postOnMainThread(Runnable runnable) {
        mHandler.post(runnable);
    }

    private RestAdapter buildRestAdapter() {
        return new RestAdapter.Builder()
                //.setLogLevel( (BuildConfig.DEBUG) ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE )
                .setRequestInterceptor(buildRequestInterceptor())
                .setClient(new OkClient())
                .setConverter(new GsonConverter(configureGson()))
                .setEndpoint(new Endpoint() {
                    @Override
                    public String getUrl() {
                        return getResource(R.string.co_base_url);
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

    private RequestInterceptor buildRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader(getResource(R.string.co_api_key_header), getResource(R.string.co_api_key));
                request.addHeader(getResource(R.string.co_token_key_header), getResource(R.string.co_token));
            }
        };
    }

    private String getResource(int resourceId) {
        return context.getResources().getString(resourceId);
    }


    /*
     * Unused - instagram / twitter together



    new Thread() {
            @Override
            public void run() {
                final CountDownLatch signal = new CountDownLatch(2);
                final BaseMultipleResponse<StoriesModel> baseMultipleResponse = new BaseMultipleResponse<StoriesModel>();
                final List<StoriesModel> models = new ArrayList<StoriesModel>();
                try {
                    mApi.getFeed("media", lng, lat, radius, new Callback<BaseMultipleResponse<StoriesModel>>() {
                        @Override
                        public void success(BaseMultipleResponse<StoriesModel> storiesModelBaseMultipleResponse, Response response) {
                            if (storiesModelBaseMultipleResponse != null && storiesModelBaseMultipleResponse.isSuccess()
                                    && storiesModelBaseMultipleResponse.getResult() != null && !storiesModelBaseMultipleResponse.getResult().isEmpty()) {
                                models.addAll(storiesModelBaseMultipleResponse.getResult());
                            }
                            signal.countDown();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            signal.countDown();
                        }
                    });

                    /*mApi.getFeed("social", lng, lat, radius, new Callback<BaseMultipleResponse<StoriesModel>>() {
                        @Override
                        public void success(BaseMultipleResponse<StoriesModel> storiesModelBaseMultipleResponse, Response response) {
                            if (storiesModelBaseMultipleResponse != null && storiesModelBaseMultipleResponse.isSuccess()
                                    && storiesModelBaseMultipleResponse.getResult() != null && !storiesModelBaseMultipleResponse.getResult().isEmpty()) {
                                models.addAll(storiesModelBaseMultipleResponse.getResult());
                            }
                            signal.countDown();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            signal.countDown();
                        }
                    });

    signal.countDown(); //TODO: Remove me if you uncomment TWITTER
    signal.await();
} catch (Exception e) {
        Timber.e(e, "Error getting nearby stories");
        baseMultipleResponse.setSuccess(false);
        } finally {
        baseMultipleResponse.setSuccess(true);
        Collections.sort(models, new Comparator<StoriesModel>() {
@Override
public int compare(StoriesModel model, StoriesModel model2) {
        return model2.getCreatedAt().compareTo(model.getCreatedAt());
        }
        });
        baseMultipleResponse.setResult(models);
        postOnMainThread(new Runnable() {
@Override
public void run() {
        callback.success(baseMultipleResponse, null);
        }
        });
        }
        }
        }.start();




     */
}
