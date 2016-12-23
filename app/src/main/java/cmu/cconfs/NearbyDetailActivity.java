package cmu.cconfs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

public class NearbyDetailActivity extends AppCompatActivity implements OnConnectionFailedListener {
    public final static String EXTRA_PICK_PLACE = "place-data";
    public final static String EXTRA_PLACE_ID = "place-id";
    private final static String TAG = NearbyDetailActivity.class.getSimpleName();

    private ImageView mPlaceImage;
    private TextView mPlaceNameTv;
    private TextView mNumberTv;
    private TextView mLocationTv;
    private TextView mWebsiteTv;
    private TextView mRatingLabelTv;
    private TextView mRatingTv;
    private TextView mPriceLevelLabelTv;
    private TextView mPriceLevelTv;

    private Toolbar mToolbar;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_detail);

        mPlaceImage = (ImageView) findViewById(R.id.image);
        mPlaceNameTv = (TextView) findViewById(R.id.place_name_tv);
        mNumberTv = (TextView) findViewById(R.id.place_phone_number);
        mLocationTv = (TextView) findViewById(R.id.place_location);
        mWebsiteTv = (TextView) findViewById(R.id.place_website);

        mRatingLabelTv = (TextView) findViewById(R.id.place_rating_label);
        mRatingTv = (TextView) findViewById(R.id.place_rating);
        mPriceLevelLabelTv = (TextView) findViewById(R.id.place_price_level_label);
        mPriceLevelTv = (TextView) findViewById(R.id.place_price_level);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // get place photo to display
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        // handle the incoming intent
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        Intent data = intent.getParcelableExtra(EXTRA_PICK_PLACE);
        if (data != null) {
            Place place = PlacePicker.getPlace(this, data);
            setUpPlaceDetailScreen(place);
        } else {
            final String placeId = intent.getStringExtra(EXTRA_PLACE_ID);
            new AsyncTask<Void, Void, Place>() {
                @Override
                protected Place doInBackground(Void... voids) {
                    return getPlaceFromId(placeId);
                }
                @Override
                protected void onPostExecute(Place place) {
                    setUpPlaceDetailScreen(place);
                }
            }.execute();
        }
    }

    private void setUpPlaceDetailScreen(Place place) {
        mPlaceNameTv.setText(place.getName());
        mNumberTv.setText(place.getPhoneNumber());
        mLocationTv.setText(place.getAddress());

        Uri uri = place.getWebsiteUri();
        if (uri != null) {
            mWebsiteTv.setText(uri.toString());
        }

        float rating = place.getRating();
        if (rating >= 0.0) {
            mRatingTv.setText(String.valueOf(rating));
        } else {
            mRatingLabelTv.setVisibility(View.INVISIBLE);
            mRatingTv.setVisibility(View.INVISIBLE);
        }

        int priceLevel = place.getPriceLevel();
        if (priceLevel >= 0) {
            mPriceLevelTv.setText(transformPriceLevel(priceLevel));
        } else {
            mPriceLevelLabelTv.setVisibility(View.INVISIBLE);
            mPriceLevelTv.setVisibility(View.INVISIBLE);
        }

        Log.d(TAG, "get a place name: " + place.getName());
//        getSupportActionBar().setTitle(place.getName());
//        mToolbar.setTitle(place.getName());

        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, place.getId()).setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
            @Override
            public void onResult(@NonNull PlacePhotoMetadataResult photos) {
                if (!photos.getStatus().isSuccess()) {
                    return;
                }
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0) {
                    photoMetadataBuffer.get(0).getScaledPhoto(mGoogleApiClient, mPlaceImage.getWidth(), mPlaceImage.getHeight()).setResultCallback(new ResultCallback<PlacePhotoResult>() {
                        @Override
                        public void onResult(@NonNull PlacePhotoResult placePhotoResult) {
                            if (!placePhotoResult.getStatus().isSuccess()) {
                                return;
                            }
                            mPlaceImage.setImageBitmap(placePhotoResult.getBitmap());
                        }
                    });
                }
            }
        });
    }

    private Place getPlaceFromId(String placeId) {
        Place place = null;
        PlaceBuffer placeBuffer = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId).await();
        if (placeBuffer.getStatus().isSuccess() && placeBuffer.getCount() > 0) {
            place = placeBuffer.get(0);
            Log.i(TAG, "Place found: " + place.getName());
        } else {
            Log.e(TAG, "Place not found: " + placeId);
        }
        return place;
    }

    private String transformPriceLevel(int level) {
        StringBuffer price = new StringBuffer();
        while (level-- > 0) {
            price.append("$");
        }
        return price.length() > 0 ? price.toString() : "free";
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Google client connection failed.");
    }
}
