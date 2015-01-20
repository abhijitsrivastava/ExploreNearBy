/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

package com.coeverywhere.glass.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coeverywhere.glass.R;
import com.coeverywhere.glass.api.model.StoriesModel;
import com.google.android.glass.widget.CardScrollAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ryaneldridge on 5/1/14.
 */
public class StoryCardAdapter extends CardScrollAdapter {

    private Context context;
    private int textViewId;
    private List<StoriesModel> items;

    public StoryCardAdapter(Context context, int textViewId, List<StoriesModel> items) {
        super();
        this.context = context;
        this.textViewId = textViewId;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        StoriesModel model = items.get(position);
        StoryViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(textViewId, viewGroup, false);
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

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getPosition(Object o) {
        return 0;
    }

    @Override
    public int getHomePosition() {
        return super.getHomePosition();
    }
}
