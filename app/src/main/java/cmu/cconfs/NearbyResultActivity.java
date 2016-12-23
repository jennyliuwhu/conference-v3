package cmu.cconfs;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cmu.cconfs.adapter.PlaceResultAdapter;

public class NearbyResultActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    public final static String EXTRA_SEARCH_TERM = "search-term";
    public final static String EXTRA_SEARCH_TYPE = "search-type";
    private final static int PERMISSION_REQUEST_FINE_LOCATION = 0;

    private final static String TAG = NearbyResultActivity.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private GoogleApiClient mGoogleApiClient;

    private ListView mPlaceListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_result);
        mRequestQueue = Volley.newRequestQueue(this);

        // connect to google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        // place list
        mPlaceListView = (ListView) findViewById(R.id.place_result_list);
        mPlaceListView.setAdapter(new PlaceResultAdapter(this, new ArrayList<Pair<String, String>>()));
        mPlaceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pair<String, String> pair = (Pair<String, String>) mPlaceListView.getAdapter().getItem(position);
                Intent i = new Intent(view.getContext(), NearbyDetailActivity.class);
                i.putExtra(NearbyDetailActivity.EXTRA_PLACE_ID, pair.first);
                startActivity(i);
            }
        });

        // handle the incoming intent accordingly
        handlerIntent(getIntent());
    }

    private void handlerIntent(Intent intent) {
        if (intent != null) {
            String term = intent.getStringExtra(EXTRA_SEARCH_TERM);
            int type = intent.getIntExtra(EXTRA_SEARCH_TYPE, 1);
            checkPermission();
            String baseUrl = null;
            switch (type) {
                case 1:
                    baseUrl = "https://maps.googleapis.com/maps/api/place/radarsearch/json?location=%f,%f&radius=50000&type=%s&key=%s";
                    break;
                case 2:
                    baseUrl = "https://maps.googleapis.com/maps/api/place/radarsearch/json?location=%f,%f&radius=50000&keyword=%s&key=%s";
                    break;
            }

            // start task to retrieve place info
            new SearchTermTask().execute(new String[] {term, baseUrl});
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    handlerIntent(getIntent());
                } else {
                    Log.e(TAG, "Error: user not grant permission");
                }
                break;
        }
    }

    // search nearby places according to geo location and query type
    private List<Pair<String, String>> searchPlaces(String term, String baseUrl) throws SecurityException {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        String url = String.format(baseUrl, lat, lon, term, getString(R.string.google_place_api_key));
        Log.d(TAG, "url: " + url);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(url, null, future, future);
        List<Pair<String, String>> placePairs = new ArrayList<>();
        mRequestQueue.add(request);
        try {
            JSONObject response = future.get(10, TimeUnit.SECONDS);
            JSONArray places = response.getJSONArray("results");
            int limit = 30;
            Log.d(TAG, "retrieve: " + places.length() + " places");
            // get place names by place ids from google api client
            for (int i = 0; i < places.length() && i < limit ; i++) {
                String placeId = places.getJSONObject(i).getString("place_id");
                PlaceBuffer placeBuffer = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId).await();
                if (placeBuffer.getStatus().isSuccess() && placeBuffer.getCount() > 0) {
                    Place place = placeBuffer.get(0);
                    placePairs.add(new Pair<String, String>(place.getId(), place.getName().toString()));
                    Log.i(TAG, "Place found: " + place.getName());
                } else {
                    Log.e(TAG, "Place not found: " + placeId);
                }
                placeBuffer.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Get place pairs: " + placePairs);
        return placePairs;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Error connect to Google API");
    }

    private class SearchTermTask extends AsyncTask<String, Void, List<Pair<String, String>>> {
        @Override
        protected List<Pair<String, String>> doInBackground(String... params) {
            return searchPlaces(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(List<Pair<String, String>> pairs) {
            if (pairs.size() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NearbyResultActivity.this, R.style.MyDialogTheme);
                builder.setTitle(R.string.no_place_avail_dialog_title);
                builder.setMessage(R.string.no_place_avail_msg);
                builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setCancelable(false);
                builder.create().show();
                return;
            }
            // have places display them
            ((PlaceResultAdapter) mPlaceListView.getAdapter()).setPlacePairs(pairs);
            ((PlaceResultAdapter) mPlaceListView.getAdapter()).notifyDataSetChanged();
        }
    }
}
