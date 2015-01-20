package com.coeverywhere.glass.activity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coeverywhere.glass.BuildConfig;
import com.coeverywhere.glass.R;
import com.coeverywhere.glass.activity.ui.CompassView;
import com.coeverywhere.glass.adapter.TuggableView;
import com.coeverywhere.glass.adapter.card.CoCard;
import com.coeverywhere.glass.api.image.RoundImageTransform;
import com.coeverywhere.glass.api.model.BaseMultipleResponse;
import com.coeverywhere.glass.api.model.BaseSingleResponse;
import com.coeverywhere.glass.api.model.Meta;
import com.coeverywhere.glass.api.model.NearbyEnabledResponse;
import com.coeverywhere.glass.api.model.PinAddress;
import com.coeverywhere.glass.api.model.StoriesModel;
import com.coeverywhere.glass.controller.LogController;
import com.coeverywhere.glass.controller.NearbyController;
import com.coeverywhere.glass.controller.OrientationManager;
import com.coeverywhere.glass.util.MathUtils;
import com.google.android.glass.app.Card;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

import com.google.android.glass.media.Sounds;
import android.media.AudioManager;


public class GlassMain extends Activity {

    private NearbyController nearbyController;
    private LocationManager locationManager;
    private SensorManager mSensorManager;

    private static final long MIN_TIME = 20000;
    private static final float MIN_DISTANCE = 0;
    private static final int DEFAULT_RADIUS = 100; //meters
    private static final int DISTANCE_CHANGE_THRESHOLD = 100; // meters.
    private float mRadius = 30.0f;
    private static final float ACCURACY_THRESHOLD = 75.0f;

    private Meta mMediaMeta;
    private boolean isPaging = false;

    private double latitude, longitude;
    private Location currentLocation;

    private List<Card> mCards;
    //private ExampleCardAdapter mAdapter;

    private TimerTask autoScrollTask = null;
    private Timer timer = null;

    public @InjectView(R.id.loading_wrapper) RelativeLayout loadingWrapper; // loading_wrapper
    public @InjectView(R.id.content_wrapper) RelativeLayout contentWrapper; // content_wrapper

    public @InjectView(R.id.compass_view) CompassView mCompassView;
    //public @InjectView(R.id.nearby_list_view) CardScrollView mListView;
    public @InjectView(R.id.story_layout) RelativeLayout storyLayoutRoot;


    public @InjectView(R.id.loading_progress) ProgressBar loadingProgress; // loading_progress
    public @InjectView(R.id.no_content) TextView noContent; // no_content

    public @InjectView(R.id.preview_text) TextView previewText; // preview_text
    public @InjectView(R.id.enabled_progress) ProgressBar enableProgress;

    private static final double LEFT_THRESHOLD = 2.5, RIGHT_THRESHOLD = -2.5;
    private static final float RADIUS_STRETCH = 2.5f;

    //Gesture Stuff
    private GestureDetector mGestureDetector;

    //Orientation Manager
    private OrientationManager mOrientationManager;

    private static final Object LOCK = new Object();

    private MathUtils.CompassDirection mCurrentDirection = null;
    private Float mCurrentHeading = null;
    private Map<MathUtils.CompassDirection, List<Card>> mDividedCards;
    private NavigableMap<Float, Card> cardNavigableMap = new TreeMap<Float, Card>();

    private RangeMap<Float, Card> cardRangeMap = TreeRangeMap.create();

    private boolean isListLoaded = false;

    private ItemViewHolder viewHolder = null;

    private Executor fixedThreadPool = Executors.newFixedThreadPool(3);

    private AudioManager mAudioManager = null;
    private CardScrollView mCardScrollView;
    private TextToSpeech tts;
    private String currentStoryDetail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_glass_main);
        setContentView(new TuggableView(this, R.layout.activity_glass_main));
        ButterKnife.inject(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new LogTree(this));
        }

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        tts = new TextToSpeech(getApplicationContext(),textToSpeechListener);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mOrientationManager = new OrientationManager(mSensorManager, locationManager);
        mOrientationManager.addOnChangedListener(orientationChangedListener);
        mOrientationManager.start();

        if (nearbyController == null) {
            nearbyController = NearbyController.getInstance(getApplicationContext());
        }
        mCompassView.setOrientationManager(mOrientationManager);
        viewHolder = new ItemViewHolder(storyLayoutRoot);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGestureDetector = createGestureDetector(this.getApplicationContext());
        if (mOrientationManager != null) {
            mOrientationManager.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOrientationManager != null) {
            mOrientationManager.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOrientationManager = null;
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private TextToSpeech.OnInitListener textToSpeechListener  = new  TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);

                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Timber.d("TTS This Language is not supported");
                } else {
                    speakOut();
                }
            } else {
                Timber.d("TTS Initilization Failed!");
            }
        }
    };

    private void speakOut() {
        String text = currentStoryDetail;
        if (tts.isSpeaking()){
            tts.stop();
        }
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private final OrientationManager.OnChangedListener orientationChangedListener =
            new OrientationManager.OnChangedListener() {
                @Override
                public void onOrientationChanged(OrientationManager orientationManager) {
                    mCompassView.setHeading(orientationManager.getHeading());
                    synchronized (LOCK) {
                        if (mCurrentDirection == null) {
                            mCurrentDirection = MathUtils.translateBearing(orientationManager.getHeading());
                        } else if (isListLoaded) {
                            //updateAdapterWithBearing(MathUtils.translateBearing(orientationManager.getHeading()), orientationManager.getHeading());
                            findValidCard(orientationManager.getHeading());
                        }
                    }
                }

                @Override
                public void onLocationChanged(OrientationManager orientationManager) {
                    Location location = orientationManager.getLocation();
                    if (location != null) {
                        Timber.d("*** Location Update: " + location + " ***");
                        if (currentLocation != null) {

                            float distanceBetween = currentLocation.distanceTo(location);
                            Timber.d("**** Distance between locations: %s *****", String.valueOf(distanceBetween));

                            if ( (currentLocation.distanceTo(location) > DISTANCE_CHANGE_THRESHOLD) ) {
                                Timber.d("**** Significant Distance Change: %s ****", String.valueOf(currentLocation.distanceTo(location)));

                                previewText.setText(R.string.locate_device_string);
                                loadingWrapper.setVisibility(View.VISIBLE);

                                contentWrapper.setVisibility(View.GONE);
                                //mListView.setVisibility(View.GONE);
                                mCompassView.setVisibility(View.GONE);
                                loadingProgress.setVisibility(View.VISIBLE);

                                isListLoaded = false;
                                currentLocation = location;
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                                Timber.d("Abhijit  > DISTANCE_CHANGE_THRESHOLD latitude : "+ latitude);
                                Timber.d("Abhijit  > DISTANCE_CHANGE_THRESHOLD longitude : "+longitude);


                                //nearbyController.enableNearby(String.valueOf(longitude), String.valueOf(latitude), enableNearby);
                                nearbyController.bootstrapNearby(String.valueOf(longitude), String.valueOf(latitude), bootstrapNearby);

                            } else if ( (location.hasAccuracy() && currentLocation.hasAccuracy()) && (location.getAccuracy() < currentLocation.getAccuracy()) ) {
                                //currentLocation = location;
                                //Timber.d("**** New location is more accurate ****" + location);
                                Timber.d("Abhijit < currentLocation.getAccuracy() ");

                            }
                        } else if (location.hasAccuracy() && location.getAccuracy() <= ACCURACY_THRESHOLD) {
                            currentLocation = location;
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            //nearbyController.enableNearby(String.valueOf(longitude), String.valueOf(latitude), enableNearby);
                            nearbyController.bootstrapNearby(String.valueOf(longitude), String.valueOf(latitude), bootstrapNearby);
                            Timber.d("Abhijit <= ACCURACY_THRESHOLD");
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(OrientationManager orientationManager) {

                }
            };

    // UI Stuff
    private Callback<BaseSingleResponse<NearbyEnabledResponse>> enableNearby = new Callback<BaseSingleResponse<NearbyEnabledResponse>>() {
        @Override
        public void success(BaseSingleResponse<NearbyEnabledResponse> nearbyEnabledResponseBaseSingleResponse, Response response) {
            if (nearbyEnabledResponseBaseSingleResponse != null && nearbyEnabledResponseBaseSingleResponse.isSuccess()
                    && nearbyEnabledResponseBaseSingleResponse.getResult() != null && nearbyEnabledResponseBaseSingleResponse.getResult().getSummary() != null) {
                nearbyController.bootstrapNearby(String.valueOf(longitude), String.valueOf(latitude), bootstrapNearby);

                String text = nearbyEnabledResponseBaseSingleResponse.getResult().getSummary();
                enableProgress.setVisibility(View.GONE);
                previewText.setVisibility(View.VISIBLE);
                previewText.setText(text);
                loadingWrapper.animate()
                        .alpha(0f)
                        .setDuration(3500)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                enableProgress.setVisibility(View.VISIBLE);
                                previewText.setVisibility(View.GONE);
                                loadingWrapper.setVisibility(View.GONE);
                                loadingWrapper.setAlpha(1f);
                                contentWrapper.setVisibility(View.VISIBLE);
                            }
                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }
                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        }).start();
            } else {
                enableProgress.setVisibility(View.VISIBLE);
                previewText.setVisibility(View.GONE);
                loadingWrapper.setVisibility(View.GONE);
                loadingWrapper.setAlpha(1f);
                contentWrapper.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Timber.e(error, "Error enabling nearby");
            enableProgress.setVisibility(View.VISIBLE);
            previewText.setVisibility(View.GONE);
            loadingWrapper.setVisibility(View.GONE);
            loadingWrapper.setAlpha(1f);
            contentWrapper.setVisibility(View.VISIBLE);
        }
    };

    private Callback<BaseMultipleResponse<StoriesModel>> bootstrapNearby = new Callback<BaseMultipleResponse<StoriesModel>>() {
        @Override
        public void success(BaseMultipleResponse<StoriesModel> storiesModelBaseMultipleResponse, Response response) {
            if (storiesModelBaseMultipleResponse != null && storiesModelBaseMultipleResponse.isSuccess()
                    && storiesModelBaseMultipleResponse.getResult() != null && !storiesModelBaseMultipleResponse.getResult().isEmpty()) {
                Long since = null;
                if (storiesModelBaseMultipleResponse.getMeta() != null) {
                    mRadius = storiesModelBaseMultipleResponse.getMeta().getRadius();
                }
                if (storiesModelBaseMultipleResponse.getMeta() != null && storiesModelBaseMultipleResponse.getMeta().getUntil() != null) {
                    since = storiesModelBaseMultipleResponse.getMeta().getUntil();
                }
                nearbyController.getNearbyStories(String.valueOf(longitude), String.valueOf(latitude), String.valueOf(mRadius), String.valueOf(since), feedCallback);
                //mRadius = storiesModelBaseMultipleResponse.getMeta().getRadius();
                //mMediaMeta = storiesModelBaseMultipleResponse.getMeta();

                /*List<Card> cards = buildCards(storiesModelBaseMultipleResponse.getResult());
                mCompassView.setCards(cards); //add all cards and let mCards to adjusted as bearing changes

                if (mCards == null) {
                    mCards = new ArrayList<Card>();
                } else {
                    mCards.clear();
                }
                List<Card> currentBearingCards = mDividedCards.get(mCurrentDirection);
                mCards.addAll(currentBearingCards);

                mAdapter = new ExampleCardAdapter(mCards, GlassMain.this.getApplicationContext());
                mListView.setAdapter(mAdapter);
                mListView.setSelection(0);
                mListView.activate();

                loadingProgress.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);

                mCompassView.setVisibility(View.VISIBLE);
                noContent.setVisibility(View.GONE);
                //mListView.setOnItemSelectedListener(itemSelectedListener); // can't use this because of paging
                isListLoaded = true; //TODO: WATCH ME!*/
            } else {
                loadingProgress.setVisibility(View.GONE);
                //mListView.setVisibility(View.GONE);
                mCompassView.setVisibility(View.GONE);
                noContent.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Timber.e(error, "Error bootstrapping");
            loadingProgress.setVisibility(View.GONE);
            //mListView.setVisibility(View.GONE);
            mCompassView.setVisibility(View.GONE);
            noContent.setVisibility(View.VISIBLE);
        }
    };

    private Callback<BaseMultipleResponse<StoriesModel>> feedCallback = new Callback<BaseMultipleResponse<StoriesModel>>() {
        @Override
        public void success(BaseMultipleResponse<StoriesModel> storiesModelBaseMultipleResponse, Response response) {
            if (storiesModelBaseMultipleResponse != null && storiesModelBaseMultipleResponse.isSuccess()
                    && storiesModelBaseMultipleResponse.getResult() != null && !storiesModelBaseMultipleResponse.getResult().isEmpty()) {

                List<Card> cards = buildCards(storiesModelBaseMultipleResponse.getResult());
                String furthestCard = convertToFeet(findFurthestStory(cards));
                mCompassView.setCards(cards); //add all cards and let mCards to adjusted as bearing changes

                if (mCards == null) {
                    mCards = new ArrayList<Card>();
                } else {
                    mCards.clear();
                }

                String text = String.format(getResources().getString(R.string.cards_count_string), String.valueOf(cards.size()), furthestCard);
                enableProgress.setVisibility(View.GONE);
                previewText.setVisibility(View.VISIBLE);
                previewText.setText(text);
                loadingWrapper.animate()
                        .alpha(0f)
                        .setDuration(3500)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                enableProgress.setVisibility(View.VISIBLE);
                                previewText.setVisibility(View.GONE);
                                loadingWrapper.setVisibility(View.GONE);
                                loadingWrapper.setAlpha(1f);
                                contentWrapper.setVisibility(View.VISIBLE);

                                loadingProgress.setVisibility(View.GONE);
                                mCompassView.setVisibility(View.VISIBLE);
                                noContent.setVisibility(View.GONE);
                            }
                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }
                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        }).start();


                /*loadingProgress.setVisibility(View.GONE);
                mCompassView.setVisibility(View.VISIBLE);
                noContent.setVisibility(View.GONE);*/
                isListLoaded = true; //TODO: WATCH ME!
            } else {
                /*loadingProgress.setVisibility(View.GONE);
                mCompassView.setVisibility(View.GONE);
                noContent.setVisibility(View.VISIBLE);*/
                enableProgress.setVisibility(View.VISIBLE);
                previewText.setVisibility(View.GONE);
                loadingWrapper.setVisibility(View.GONE);
                loadingWrapper.setAlpha(1f);
                contentWrapper.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Timber.e(error, "Error getting feed");
            loadingProgress.setVisibility(View.GONE);
            //mListView.setVisibility(View.GONE);
            mCompassView.setVisibility(View.GONE);
            noContent.setVisibility(View.VISIBLE);
        }
    };

    private List<Card> buildCards(List<StoriesModel> models) {
        List<Card> cards = new ArrayList<Card>();
        if (cardRangeMap != null) {
            cardRangeMap.clear();
        }
        if (cardNavigableMap != null) {
            cardNavigableMap.clear();
        }
        for (StoriesModel model : models) {
            CoCard card = new CoCard(this.getApplicationContext(), model.getAttachedImageThumb(), model.getStoryId());
            card.setText(model.getContent());
            card.setImageLayout(Card.ImageLayout.FULL);

            if (model.getLocation() != null
                    && model.getLocation().getCoordinates() != null
                    && model.getLocation().getCoordinates().length == 2) {
                double latitude = model.getLocation().getCoordinates()[1];
                double longitude = model.getLocation().getCoordinates()[0];
                Location storyLoc = new Location("gps");
                storyLoc.setLatitude(latitude);
                storyLoc.setLongitude(longitude);
                Float rawDistance = storyLoc.distanceTo(currentLocation);
                card.setRawDistance(rawDistance);
                card.setDistance(convertToFeet(rawDistance));
                float bearing = MathUtils.getBearing(mOrientationManager.getLocation().getLatitude(), mOrientationManager.getLocation().getLongitude(),
                        storyLoc.getLatitude(), storyLoc.getLongitude());
                card.setBearing(bearing);
                card.setCompassDirection(MathUtils.translateBearing(bearing));
                card.setHeight(rand());
                card.setPaintColor(new Random().nextInt(3));
                card.setModel(model);
            }

            if (model.getUser() != null) {
                card.setUser(model.getUser());
            }
            card.setSourceLogo(model.getSourceLogoUrl());
            card.setCreatedAt(model.getCreatedAt());

            if (card.getCompassDirection() != null) {
                cards.add(card);
                cardNavigableMap.put(card.getBearing(), card);
                cardRangeMap.put(com.google.common.collect.Range.closed( (card.getBearing() - RADIUS_STRETCH), (card.getBearing() + RADIUS_STRETCH) ), card);
            }
        }
        if (mDividedCards != null) {
            mDividedCards.clear();
        }
        mDividedCards = divideCards(cards);
        return cards;
    }

    private Card removeLastDuplicateCard(CoCard lastCurrentCard, CoCard firstNewCard) {
        if (lastCurrentCard.getStoryId().equals(firstNewCard.getStoryId())) {
            return firstNewCard;
        } else {
            return null;
        }
    }

    private String convertToFeet(float distance) {
        double calc = Math.floor((double) distance * 3.2808);
        String str = String.format("%d", (int)calc);
        return str;
    }

    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                switch (gesture) {
                    case TWO_TAP:
                        if (currentLocation != null) {
                            previewText.setText(R.string.locate_device_string);
                            loadingWrapper.setVisibility(View.VISIBLE);

                            contentWrapper.setVisibility(View.GONE);
                            //mListView.setVisibility(View.GONE);
                            mCompassView.setVisibility(View.GONE);
                            loadingProgress.setVisibility(View.VISIBLE);

                            //stopAutoScroller();

                            /*nearbyController.enableNearby(String.valueOf(currentLocation.getLongitude()),
                                    String.valueOf(currentLocation.getLatitude()), enableNearby);*/
                            nearbyController.bootstrapNearby(String.valueOf(currentLocation.getLongitude()),
                                    String.valueOf(currentLocation.getLatitude()), bootstrapNearby);
                        }
                        return true;

                    case TAP:
                        if (contentWrapper.getVisibility() == View.VISIBLE){
                            openOptionsMenu();
                        }else{
                            mAudioManager.playSoundEffect(Sounds.DISALLOWED);
                        }

                        //startInstructionActivity();
                        return true;
                    case SWIPE_RIGHT:
                        //startInstructionActivity();
                        return true;
                    case SWIPE_LEFT:
                        //startInstructionActivity();
                        return true;
                    default:
                        return false;
                }
            }
        });

        return gestureDetector;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        menu.clear();
        menu.add(0, 1, Menu.NONE,
                getResources().getString(R.string.menu_about_us));
        menu.add(0, 2, Menu.NONE,
                getResources().getString(R.string.menu_instruction_activity));
        menu.add(0, 3, Menu.NONE,
                getResources().getString(R.string.menu_read_loud));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        mAudioManager.playSoundEffect(Sounds.TAP);
        Intent intent = null;

        switch (item.getItemId()) {
            case 1: // start menu_about_us activity
                //finish();
                break;

            case 2: // start menu_instruction_activity activity
                startInstructionActivity();
                break;

            case 3: // start menu_read_loud activity
                //mTextToSpeech.speak();
                speakOut();
                break;

            default:
                Timber.d("Default option is selected");
                break;
        }

        return super.onOptionsItemSelected(item);
    }





    private void startInstructionActivity() {
        Intent instructionIntent = new Intent(GlassMain.this, InstructionActivity.class);
        startActivity(instructionIntent);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    private Map<MathUtils.CompassDirection, List<Card>> divideCards(List<Card> cards) {
        Map<MathUtils.CompassDirection, List<Card>> parent = new HashMap<MathUtils.CompassDirection, List<Card>>();
        List<Card> N = new ArrayList<Card>();
        parent.put(MathUtils.CompassDirection.N, N);
        List<Card> NE = new ArrayList<Card>();
        parent.put(MathUtils.CompassDirection.NE, NE);
        List<Card> E = new ArrayList<Card>();
        parent.put(MathUtils.CompassDirection.E, E);
        List<Card> SE = new ArrayList<Card>();
        parent.put(MathUtils.CompassDirection.SE, SE);
        List<Card> S = new ArrayList<Card>();
        parent.put(MathUtils.CompassDirection.S, S);
        List<Card> SW = new ArrayList<Card>();
        parent.put(MathUtils.CompassDirection.SW, SW);
        List<Card> W = new ArrayList<Card>();
        parent.put(MathUtils.CompassDirection.W, W);
        List<Card> NW = new ArrayList<Card>();
        parent.put(MathUtils.CompassDirection.NW, NW);

        for (Card c : cards) {
            CoCard co = (CoCard)c;
            switch (co.getCompassDirection()) {
                case N:
                    N.add(co);
                    break;
                case NE:
                    NE.add(co);
                    break;
                case E:
                    E.add(co);
                    break;
                case SE:
                    SE.add(co);
                    break;
                case S:
                    S.add(co);
                    break;
                case SW:
                    SW.add(co);
                    break;
                case W:
                    W.add(co);
                    break;
                case NW:
                    NW.add(co);
                    break;
            }
        }
        return parent;
    }

    private int rand() {
        return new Random().nextInt( (15 - 1) + 1 ) + 1;
    }

    private void updateAdapterWithBearing(final MathUtils.CompassDirection compassDirection, float heading) {
        /*if (compassDirection != null && mDividedCards != null && isListLoaded) {
            if (mCurrentDirection != null && compassDirection != mCurrentDirection) {
                List<Card> cards = mDividedCards.get(compassDirection);
                mCards.clear();
                mCards.addAll(cards);
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(0);
                mCurrentDirection = compassDirection;
                //startAutoScroller();
            }
        }*/
    }

    private static class LogTree extends Timber.HollowTree {
        private Context context;
        public LogTree(Context context) {
            this.context = context;
        }

        @Override
        public void d(String message, Object... args) {
            super.d(message, args);
        }

        @Override
        public void d(Throwable t, String message, Object... args) {
            super.d(t, message, args);
        }

        @Override
        public void i(String message, Object... args) {
            super.i(message, args);
        }

        @Override
        public void i(Throwable t, String message, Object... args) {
            super.i(t, message, args);
        }

        @Override
        public void w(String message, Object... args) {
            super.w(message, args);
        }

        @Override
        public void w(Throwable t, String message, Object... args) {
            super.w(t, message, args);
        }

        @Override
        public void e(String message, Object... args) {
            super.e(message, args);
        }

        @Override
        public void e(Throwable t, String message, Object... args) {
            super.e(t, message, args);
        }
    }

    private void autoScroller() {
        /*int sectionCount = mAdapter.getCount();
        if (sectionCount > 1) {
            int current = mListView.getSelectedItemPosition();
            if (current == sectionCount) {
                mListView.setSelection(0); //reset to first card
            } else {
                current++;
                mListView.setSelection(current);
            }
            mAdapter.notifyDataSetChanged();
        }*/
    }

    private void startAutoScroller() {
        if (autoScrollTask != null) {
            autoScrollTask.cancel();
        }
        autoScrollTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        autoScroller();
                    }
                });
            }
        };
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(autoScrollTask, 3500, 3500);
    }

    private void stopAutoScroller() {
        if (autoScrollTask != null) {
            autoScrollTask.cancel();
        }
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }

    private void findValidCard(final float heading) {
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (LOCK) {
                    if (isListLoaded) {
                        Map.Entry<com.google.common.collect.Range<Float>, Card> entry = cardRangeMap.getEntry(heading);

                        if (entry != null) {
                            final CoCard c = (CoCard)entry.getValue();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mCompassView.setCurrentCard(c);
                                    //Timber.d("**** SHOWING CARD: %s ****", ((CoCard)c).getModel().getTitle() );
                                    viewHolder.keepExploringWrapper.setVisibility(View.GONE);
                                    viewHolder.leftColumn.setVisibility(View.VISIBLE);
                                    viewHolder.contentWrapper.setVisibility(View.VISIBLE);
                                    viewHolder.footerWrapper.setVisibility(View.VISIBLE);
                                    paintHoveredCard(c);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //mCompassView.setCurrentCard(null);
                                    viewHolder.leftColumn.setVisibility(View.GONE);
                                    viewHolder.contentWrapper.setVisibility(View.GONE);
                                    viewHolder.footerWrapper.setVisibility(View.GONE);
                                    viewHolder.keepExploringWrapper.setVisibility(View.VISIBLE);
                                    currentStoryDetail = getResources().getString(R.string.keep_exploring).toString();
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void paintHoveredCard(Card card) {
        try {
            if (card != null) {
                CoCard coCard = (CoCard) card;
                viewHolder.content.setText(coCard.getText());
                currentStoryDetail = coCard.getText().toString();
                if (coCard.getUrl() != null) {
                    viewHolder.leftColumn.setVisibility(View.VISIBLE);
                    viewHolder.imageProgress.setVisibility(View.VISIBLE);
                    Picasso.with(GlassMain.this)
                            .load(coCard.getUrl())
                            .fit()
                            .centerCrop()
                            .into(viewHolder.image, new com.squareup.picasso.Callback() {
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
                        Picasso.with(GlassMain.this)
                                .load(coCard.getUser().getAvatarUrl())
                                .transform(new RoundImageTransform(100, 0))
                                .into(viewHolder.userAvatar, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError() {
                                        viewHolder.userAvatar.setImageDrawable(getResources().getDrawable(R.drawable.img_profile_blank_avatar));
                                    }
                                });
                    }
                    if (coCard.getUser().getName() != null) {
                        viewHolder.userName.setText(coCard.getUser().getName());
                    }
                }
               /* if (coCard.getSourceLogo() != null) {
                    Picasso.with(GlassMain.this)
                            .load(coCard.getSourceLogo())
                            .into(viewHolder.attributionImage);
                }*/

                if (coCard.getDistance() != null && !"".equalsIgnoreCase(coCard.getDistance())) {
                    viewHolder.footer.setText(coCard.getDistance() + "ft");
                }

                viewHolder.timestamp.setText(DateUtils.getRelativeTimeSpanString(coCard.getCreatedAt() * 1000));

                StoriesModel model = coCard.getModel();
                if (model.getPin() != null && model.getPin().getName() != null) {
                    viewHolder.pin.setText(model.getPin().getName());
                    viewHolder.pinWrapper.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.pin.setText("");
                    viewHolder.pinWrapper.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            Timber.e(e, "Error painting hovered card");
        }
    }

    static class ItemViewHolder {
        @InjectView(R.id.left_column) RelativeLayout leftColumn;
        @InjectView(R.id.content_wrapper) RelativeLayout contentWrapper;
        @InjectView(R.id.footer_container) RelativeLayout footerWrapper;
        @InjectView(R.id.keep_exploring_wrapper) RelativeLayout keepExploringWrapper;

        @InjectView(R.id.image) ImageView image;
        @InjectView(R.id.image_progress) ProgressBar imageProgress;
        @InjectView(R.id.content) TextView content;
        @InjectView(R.id.footer) TextView footer;
        @InjectView(R.id.pin_wrapper) LinearLayout pinWrapper;
        @InjectView(R.id.pin) TextView pin;
        @InjectView(R.id.timestamp) TextView timestamp;

        @InjectView(R.id.user_avatar) ImageView userAvatar;
        @InjectView(R.id.user_name) TextView userName;

        //@InjectView(R.id.attribution_image) ImageView attributionImage;

        public ItemViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }

    private String buildPinAddress(PinAddress address) {
        StringBuilder sb = new StringBuilder();
        if (address.getAddressLine() != null && address.getAddressLine().length() > 0) {
            sb.append(address.getAddressLine());
        }
        if (address.getCity() != null && address.getCity().length() > 0) {
            sb.append(" ").append(address.getCity());
        }
        if (address.getState() != null && address.getState().length() > 0) {
            sb.append(", ").append(address.getState());
        }
        return sb.toString();
    }

    private Float findFurthestStory(List<Card> cards) {
        Float furthest = null;

        for (int i = 0; i < cards.size(); i++) {
            CoCard coCard = (CoCard) cards.get(i);
            if (i == 0) {
                furthest = coCard.getRawDistance();
            } else {
                if (coCard.getRawDistance() > furthest) {
                    furthest = coCard.getRawDistance();
                }
            }
        }
        return furthest;
    }

    //unused
    /*private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

            int currentCount = mListView.getAdapter().getCount() - 3;
            if (position == currentCount) {
                if (mMediaMeta != null && !isPaging) {
                    isPaging = true;

                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };*/

}
