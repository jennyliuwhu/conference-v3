package cmu.cconfs;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmu.cconfs.adapter.ProfileListAdapter;
import cmu.cconfs.model.parseModel.Profile;

public class NetworkingActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {
    private final static String TAG = NetworkingActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private Map<String, Integer> mIndexToPos;
    private ListView mProfileListView;
    private ProfileListAdapter mListAdapter;
    private LinearLayout mIndexSideBar;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networking);

        mIndexToPos = new HashMap<>();
        mProfileListView = (ListView) findViewById(R.id.profile_list);
        mIndexSideBar = (LinearLayout) findViewById(R.id.side_index);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);

        fetchSharedProfiles();

        mProfileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Profile profile = mListAdapter.getProfiles().get(i);
                    Log.d(TAG, "Profile name: " + profile.getFullName());
                    Intent intent = new Intent(NetworkingActivity.this, NetworkingProfileActivity.class);
                    intent.putExtra(NetworkingProfileActivity.EXTRA_PROFILE_USERNAME, profile.getParseUser().fetchIfNeeded().getUsername());
                    startActivity(intent);
                } catch (ParseException e) {
                    Log.e(TAG, e.getMessage());
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
        mListAdapter.filter(newText);
        mListAdapter.notifyDataSetChanged();
        updateSideIndex();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mListAdapter.filter(query);
        mListAdapter.notifyDataSetChanged();
        updateSideIndex();
        return true;
    }

    private void fetchSharedProfiles() {
        mProgressDialog.show();

        new AsyncTask<Void, Void, List<Profile>>() {
            @Override
            protected List<Profile> doInBackground(Void... voids) {
                List<Profile> fetched = new ArrayList<Profile>();
                try {
                    // query profiles that are to be shared
                    ParseQuery<Profile> query = Profile.getQuery();
                    query.whereEqualTo(Profile.SHARE_OPTION_KEY, true);
                    query.addAscendingOrder(Profile.FULL_NAME_KEY);
                    fetched = query.find();
                } catch (ParseException e) {
                    Log.d(TAG, e.getMessage());
                }
                return fetched;
            }

            @Override
            protected void onPostExecute(List<Profile> profiles) {
                mListAdapter = new ProfileListAdapter(NetworkingActivity.this, profiles);
                mProfileListView.setAdapter(mListAdapter);
                updateSideIndex();
                mListAdapter.notifyDataSetChanged();
                mProgressDialog.dismiss();
            }
        }.execute();
    }

    private void updateSideIndex() {
        mIndexToPos.clear();
        // get all users with different first character
        List<Profile> profiles = mListAdapter.getProfiles();
        for (int i = 0; i < profiles.size(); i++) {
            String fullName = profiles.get(i).getFullName();
            String index = fullName.substring(0, 1).toUpperCase();

            if (!mIndexToPos.containsKey(index)) {
                mIndexToPos.put(index, i);
            }
        }

        // update the side index bar view
        mIndexSideBar.removeAllViews();
        TextView textView;
        List<String> indexList = new ArrayList<String>(mIndexToPos.keySet());
        Collections.sort(indexList);
        for (String index : indexList) {
            textView = (TextView) getLayoutInflater().inflate(R.layout.side_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(this);
            mIndexSideBar.addView(textView);
        }
    }

    @Override
    public void onClick(View view) {
        TextView selectedIndex = (TextView) view;
        mProfileListView.setSelection(mIndexToPos.get(selectedIndex.getText()));
        Toast.makeText(view.getContext(), "To position: " + mIndexToPos.get(selectedIndex.getText()), Toast.LENGTH_SHORT).show();
    }
}