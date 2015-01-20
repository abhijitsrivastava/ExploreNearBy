/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

package com.coeverywhere.glass.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coeverywhere.glass.R;
import com.coeverywhere.glass.adapter.card.CoCard;
import com.coeverywhere.glass.api.image.RoundImageTransform;
import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ryaneldridge on 5/1/14.
 */
public class ExampleCardAdapter extends CardScrollAdapter {

    private List<Card> mCards;
    private Context context;

    public ExampleCardAdapter(List<Card> cards, Context context) {
        super();
        this.mCards = cards;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mCards.size();
    }

    @Override
    public Object getItem(int i) {
        return mCards.get(i);
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup viewGroup) {
        CoCard coCard = (CoCard)mCards.get(position);

        final StoryViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.story_item, viewGroup, false);
            viewHolder = new StoryViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (StoryViewHolder) convertView.getTag();
        }

        viewHolder.content.setText(coCard.getText());
        if (coCard.getUrl() != null) {
            viewHolder.leftColumn.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(coCard.getUrl())
                    .fit()
                    .centerCrop()
                    .into(viewHolder.image, new Callback() {
                        @Override
                        public void onSuccess() {
                            viewHolder.imageProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            viewHolder.imageProgress.setVisibility(View.GONE);
                        }
                    });
        } else {
            viewHolder.leftColumn.setVisibility(View.GONE);
        }

        if (coCard.getUser() != null) {
            if (coCard.getUser().getAvatarUrl() != null) {
                Picasso.with(context)
                        .load(coCard.getUser().getAvatarUrl())
                        .transform(new RoundImageTransform(100, 0))
                        .into(viewHolder.userAvatar);
            }
            if (coCard.getUser().getName() != null) {
                viewHolder.userName.setText(coCard.getUser().getName());
            }
        }
        if (coCard.getSourceLogo() != null) {
            Picasso.with(context)
                    .load(coCard.getSourceLogo())
                    .into(viewHolder.attributionImage);
        }

        if (coCard.getDistance() != null && !"".equalsIgnoreCase(coCard.getDistance())) {
            viewHolder.footer.setText(coCard.getDistance() + "ft");
        }

        viewHolder.timestamp.setText(DateUtils.getRelativeTimeSpanString( coCard.getCreatedAt() * 1000 ));

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return mCards.get(position).getItemViewType();
    }



    static class StoryViewHolder {

        @InjectView(R.id.left_column) RelativeLayout leftColumn;
        @InjectView(R.id.image) ImageView image;
        @InjectView(R.id.image_progress) ProgressBar imageProgress;
        @InjectView(R.id.content) TextView content;
        @InjectView(R.id.footer) TextView footer;
        @InjectView(R.id.timestamp) TextView timestamp;

        @InjectView(R.id.user_avatar) ImageView userAvatar;
        @InjectView(R.id.user_name) TextView userName;

        @InjectView(R.id.attribution_image) ImageView attributionImage;

        public StoryViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

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
        return mCards.indexOf(o);
    }

    @Override
    public int getHomePosition() {
        return super.getHomePosition();
    }
}
