/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

package com.coeverywhere.glass.activity.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.coeverywhere.glass.R;
import com.coeverywhere.glass.adapter.card.CoCard;
import com.coeverywhere.glass.controller.OrientationManager;
import com.coeverywhere.glass.util.MathUtils;
import com.google.android.glass.app.Card;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryaneldridge on 5/19/14.
 */
public class CompassView extends View {

    /** Various dimensions and other drawing-related constants. */
    private static final float NEEDLE_WIDTH = 4;
    private static final float NEEDLE_HEIGHT = 20;
    private static final int NEEDLE_COLOR = Color.RED;
    private static final float TICK_WIDTH = 2;
    private static final float TICK_HEIGHT = 10;
    //private static final float DIRECTION_TEXT_HEIGHT = 84.0f;
    private static final float DIRECTION_TEXT_HEIGHT = 40.0f;
    private static final float PLACE_TEXT_HEIGHT = 22.0f;
    private static final float PLACE_PIN_WIDTH = 14.0f;
    private static final float PLACE_TEXT_LEADING = 4.0f;
    private static final float PLACE_TEXT_MARGIN = 8.0f;


    /**
     * If the difference between two consecutive headings is less than this value, the canvas will
     * be redrawn immediately rather than animated.
     */
    private static final float MIN_DISTANCE_TO_ANIMATE = 15.0f;

    /** The actual heading that represents the direction that the user is facing. */
    private float mHeading;

    /**
     * Represents the heading that is currently being displayed when the view is drawn. This is
     * used during animations, to keep track of the heading that should be drawn on the current
     * frame, which may be different than the desired end point.
     */
    private float mAnimatedHeading;

    private OrientationManager mOrientation;

    private final Paint mPaint;
    private final Paint mTickPaint;
    private final Path mPath;
    private final TextPaint mPlacePaint;
    private final TextPaint mClosePaint;
    private final TextPaint mCyanPaint;
    //private final Bitmap mPlaceBitmap;
    private final Rect mTextBounds;
    private final List<Rect> mAllBounds;
    private final NumberFormat mDistanceFormat;
    private final String[] mDirections;
    private final ValueAnimator mAnimator;

    private static final Object LOCK = new Object();

    private final TextPaint[] mPaintChoices = new TextPaint[3];

    private CoCard mCurrentCard = null;

    /**
     * Map of all the cards returned. Cards have CompassDirection
     */
    private List<Card> mCards;

    public CompassView(Context context) {
        this(context, null, 0);
    }

    public CompassView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(DIRECTION_TEXT_HEIGHT);
        mPaint.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));

        mTickPaint = new Paint();
        mTickPaint.setStyle(Paint.Style.STROKE);
        mTickPaint.setStrokeWidth(TICK_WIDTH);
        mTickPaint.setAntiAlias(true);
        mTickPaint.setColor(Color.WHITE);

        mPlacePaint = new TextPaint();
        mPlacePaint.setStyle(Paint.Style.FILL);
        mPlacePaint.setAntiAlias(true);
        mPlacePaint.setColor(Color.GRAY);
        //mPlacePaint.setTextSize(PLACE_TEXT_HEIGHT);
        //mPlacePaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));

        mClosePaint = new TextPaint();
        mClosePaint.setStyle(Paint.Style.FILL);
        mClosePaint.setAntiAlias(true);
        mClosePaint.setColor(Color.BLUE);

        mCyanPaint = new TextPaint();
        mCyanPaint.setStyle(Paint.Style.FILL);
        mCyanPaint.setAntiAlias(true);
        mCyanPaint.setColor(Color.CYAN);

        mPaintChoices[0] = mPlacePaint;
        mPaintChoices[1] = mClosePaint;
        mPaintChoices[2] = mCyanPaint;


        mPath = new Path();
        mTextBounds = new Rect();
        mAllBounds = new ArrayList<Rect>();

        mDistanceFormat = NumberFormat.getNumberInstance();
        mDistanceFormat.setMinimumFractionDigits(0);
        mDistanceFormat.setMaximumFractionDigits(1);

        //mPlaceBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.place_mark);

        // We use NaN to indicate that the compass is being drawn for the first
        // time, so that we can jump directly to the starting orientation
        // instead of spinning from a default value of 0.
        mAnimatedHeading = Float.NaN;

        mDirections = context.getResources().getStringArray(R.array.direction_abbreviations);

        mAnimator = new ValueAnimator();
        setupAnimator();
    }

    /**
     * Sets the instance of {@link OrientationManager} that this view will use to get the current
     * heading and location.
     *
     * @param orientationManager the instance of {@code OrientationManager} that this view will use
     */
    public void setOrientationManager(OrientationManager orientationManager) {
        mOrientation = orientationManager;
    }

    /**
     * Gets the current heading in degrees.
     *
     * @return the current heading.
     */
    public float getHeading() {
        return mHeading;
    }

    /**
     * Sets the current heading in degrees and redraws the compass. If the angle is not between 0
     * and 360, it is shifted to be in that range.
     *
     * @param degrees the current heading
     */
    public void setHeading(float degrees) {
        mHeading = MathUtils.mod(degrees, 360.0f);
        animateTo(mHeading);
    }

    /**
     * Gets the current list of cards
     * @return cards
     */
    public List<Card> getCards() {
        return mCards;
    }

    /**
     * Sets the list of cards to be drawn as points
     * @param mCards cards
     */
    public void setCards(List<Card> mCards) {
        if (this.mCards != null) {
            this.mCards.clear();
        }
        this.mCards = mCards;
    }

    public void setCurrentCard(CoCard currentCard) {
        this.mCurrentCard = currentCard;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // The view displays 90 degrees across its width so that one 90 degree head rotation is
        // equal to one full view cycle.
        float pixelsPerDegree = getWidth() / 90.0f;
        float centerX = getWidth() / 2.0f;
        float centerY = getHeight() / 2.0f;

        canvas.save();
        canvas.translate(-mAnimatedHeading * pixelsPerDegree + centerX, centerY);

        // In order to ensure that places on a boundary close to 0 or 360 get drawn correctly, we
        // draw them three times; once to the left, once at the "true" bearing, and once to the
        // right.
        for (int i = -1; i <= 1; i++) {
            //drawPlaces(canvas, pixelsPerDegree, i * pixelsPerDegree * 360);
            drawCardIndicators(canvas, pixelsPerDegree, i * pixelsPerDegree * 360);
        }

        drawCompassDirections(canvas, pixelsPerDegree);

        canvas.restore();

        mPaint.setColor(NEEDLE_COLOR);
        drawNeedle(canvas, false);
        drawNeedle(canvas, true);
    }

    /**
     * Draws the compass direction strings (N, NW, W, etc.).
     *
     * @param canvas the {@link Canvas} upon which to draw
     * @param pixelsPerDegree the size, in pixels, of one degree step
     */
    private void drawCompassDirections(Canvas canvas, float pixelsPerDegree) {
        float degreesPerTick = 360.0f / mDirections.length;

        mPaint.setColor(Color.WHITE);

        // We draw two extra ticks/labels on each side of the view so that the
        // full range is visible even when the heading is approximately 0.
        for (int i = -2; i <= mDirections.length + 2; i++) {
            if (MathUtils.mod(i, 2) == 0) {
                // Draw a text label for the even indices.
                String direction = mDirections[MathUtils.mod(i, mDirections.length)];
                mPaint.getTextBounds(direction, 0, direction.length(), mTextBounds);

                canvas.drawText(direction,
                        i * degreesPerTick * pixelsPerDegree - mTextBounds.width() / 2,
                        mTextBounds.height() / 2, mPaint);
            } else {
                // Draw a tick mark for the odd indices.
                canvas.drawLine(i * degreesPerTick * pixelsPerDegree, -TICK_HEIGHT / 2, i
                        * degreesPerTick * pixelsPerDegree, TICK_HEIGHT / 2, mTickPaint);
            }
        }
    }

    private void drawCardIndicators(Canvas canvas, float pixelsPerDegree, float offset) {
        if (mCards != null && !mCards.isEmpty()) {
            synchronized (LOCK) {
                /*Card card = mCards.get(0);
                CoCard coCard = (CoCard) card;

                float bearing = coCard.getBearing();
                float heading = mOrientation.getHeading();
                float diff = bearing - heading;
                if (diff < LEFT_THRESHOLD && diff > RIGHT_THRESHOLD) {
                    Log.d("COMPASS_VIEW", "**** CENTERED OVER VIEW ****");
                    publishResults(card);
                } else {
                    publishResults(null);
                }
                canvas.drawCircle( (offset + bearing * pixelsPerDegree), coCard.getHeight(), 5, getRandomPaint(coCard.getPaintColor()));*/

                for (Card card : mCards) {
                    CoCard coCard = (CoCard) card;
                    float bearing = coCard.getBearing();

                    int radius = 5;
                    if (mCurrentCard != null && coCard.equals(mCurrentCard)) {
                        radius = 10;
                    }

                    //NOTE: Just drawing the circle is all that is necessary
                    canvas.drawCircle( (offset + bearing * pixelsPerDegree), coCard.getHeight(), radius, getRandomPaint(coCard.getPaintColor()));
                }
            }
        }
    }

    /**
     * Draws a needle that is centered at the top or bottom of the compass.
     *
     * @param canvas the {@link Canvas} upon which to draw
     * @param bottom true to draw the bottom needle, or false to draw the top needle
     */
    private void drawNeedle(Canvas canvas, boolean bottom) {
        float centerX = getWidth() / 2.0f;
        float origin;
        float sign;

        // Flip the vertical coordinates if we're drawing the bottom needle.
        if (bottom) {
            origin = getHeight();
            sign = -1;
        } else {
            origin = 0;
            sign = 1;
        }

        float needleHalfWidth = NEEDLE_WIDTH / 2;

        mPath.reset();
        mPath.moveTo(centerX - needleHalfWidth, origin);
        mPath.lineTo(centerX - needleHalfWidth, origin + sign * (NEEDLE_HEIGHT - 4));
        mPath.lineTo(centerX, origin + sign * NEEDLE_HEIGHT);
        mPath.lineTo(centerX + needleHalfWidth, origin + sign * (NEEDLE_HEIGHT - 4));
        mPath.lineTo(centerX + needleHalfWidth, origin);
        mPath.close();

        canvas.drawPath(mPath, mPaint);
    }

    /**
     * Sets up a {@link ValueAnimator} that will be used to animate the compass
     * when the distance between two sensor events is large.
     */
    private void setupAnimator() {
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(250);

        // Notifies us at each frame of the animation so we can redraw the view.
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mAnimatedHeading = MathUtils.mod((Float) mAnimator.getAnimatedValue(), 360.0f);
                invalidate();
            }
        });

        // Notifies us when the animation is over. During an animation, the user's head may have
        // continued to move to a different orientation than the original destination angle of the
        // animation. Since we can't easily change the animation goal while it is running, we call
        // animateTo() again, which will either redraw at the new orientation (if the difference is
        // small enough), or start another animation to the new heading. This seems to produce
        // fluid results.
        mAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animator) {
                animateTo(mHeading);
            }
        });
    }

    /**
     * Animates the view to the specified heading, or simply redraws it immediately if the
     * difference between the current heading and new heading are small enough that it wouldn't be
     * noticeable.
     *
     * @param end the desired heading
     */
    private void animateTo(float end) {
        // Only act if the animator is not currently running. If the user's orientation changes
        // while the animator is running, we wait until the end of the animation to update the
        // display again, to prevent jerkiness.
        if (!mAnimator.isRunning()) {
            float start = mAnimatedHeading;
            float distance = Math.abs(end - start);
            float reverseDistance = 360.0f - distance;
            float shortest = Math.min(distance, reverseDistance);

            if (Float.isNaN(mAnimatedHeading) || shortest < MIN_DISTANCE_TO_ANIMATE) {
                // If the distance to the destination angle is small enough (or if this is the
                // first time the compass is being displayed), it will be more fluid to just redraw
                // immediately instead of doing an animation.
                mAnimatedHeading = end;
                invalidate();
            } else {
                // For larger distances (i.e., if the compass "jumps" because of sensor calibration
                // issues), we animate the effect to provide a more fluid user experience. The
                // calculation below finds the shortest distance between the two angles, which may
                // involve crossing 0/360 degrees.
                float goal;

                if (distance < reverseDistance) {
                    goal = end;
                } else if (end < start) {
                    goal = end + 360.0f;
                } else {
                    goal = end - 360.0f;
                }

                mAnimator.setFloatValues(start, goal);
                mAnimator.start();
            }
        }
    }

    private TextPaint getRandomPaint(int choice) {
        return mPaintChoices[choice];
    }

}
