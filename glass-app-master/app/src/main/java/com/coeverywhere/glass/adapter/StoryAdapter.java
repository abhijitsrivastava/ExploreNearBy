/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

package com.coeverywhere.glass.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coeverywhere.glass.R;
import com.coeverywhere.glass.api.model.StoriesModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ryaneldridge on 5/1/14.
 */
public class StoryAdapter extends ArrayAdapter<StoriesModel> {

    private Context context;
    private int textViewId;
    private List<StoriesModel> models;

    public StoryAdapter(Context context, int textViewId, List<StoriesModel> items) {
        super(context, textViewId, items);
        this.context = context;
        this.textViewId = textViewId;
        this.models = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StoriesModel model = models.get(position);
        StoryViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(textViewId, parent, false);
            viewHolder = new StoryViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (StoryViewHolder) convertView.getTag();
        }

        viewHolder.content.setText(model.getContent());
        Picasso.with(context)
                .load(model.getAttachedImageThumb())
                .into(viewHolder.image);

        return convertView;
    }

    static class StoryViewHolder {

        @InjectView(R.id.image) ImageView image;
        @InjectView(R.id.content) TextView content;

        public StoryViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }

}
