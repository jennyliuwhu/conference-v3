package cmu.cconfs;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cmu.cconfs.adapter.PlaceTypeAdapter;

public class NearbyActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private final static String TAG = NearbyActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private ListView mPlaceTypeListView;

    private final static int REQUEST_PLACE_PICKER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mPlaceTypeListView = (ListView) findViewById(R.id.place_type_list);
        mPlaceTypeListView.setAdapter(new PlaceTypeAdapter(this));
        mPlaceTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] types = PlaceTypeAdapter.getTypes();
                Toast.makeText(getApplicationContext(), "click " + types[position], Toast.LENGTH_LONG).show();
                Intent i = new Intent(NearbyActivity.this, NearbyResultActivity.class);
                if (position == 0) {
                    // start place picker
                    pickPlace();
                } else {
                    // put the place type and search type
                    i.putExtra(NearbyResultActivity.EXTRA_SEARCH_TYPE, 1);
                    i.putExtra(NearbyResultActivity.EXTRA_SEARCH_TERM, PlaceTypeAdapter.getSupportedSearchType(position));
                    startActivity(i);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager sm = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(sm.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        Log.d(TAG, "Search text changed");
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
            Log.d(TAG, "Search text submitted");
            String encoded = URLEncoder.encode(query, "UTF-8").replace("+", "%20");
            Intent i = new Intent(NearbyActivity.this, NearbyResultActivity.class);
            i.putExtra(NearbyResultActivity.EXTRA_SEARCH_TYPE, 2);
            i.putExtra(NearbyResultActivity.EXTRA_SEARCH_TERM, encoded);
            startActivity(i);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error encoding url: " + e.getMessage());
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PLACE_PICKER:
                if (resultCode == RESULT_OK) {
                    Intent i = new Intent(this, NearbyDetailActivity.class);
                    i.putExtra(NearbyDetailActivity.EXTRA_PICK_PLACE, data);
                    startActivity(i);
                }
                break;
        }
    }

    private void pickPlace() {
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent i = intentBuilder.build(this);
            startActivityForResult(i, REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }


}
