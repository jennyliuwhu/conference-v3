package cmu.cconfs;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmu.cconfs.adapter.ProfileListAdapter;

public class NetworkingActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {

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
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void onClick(View view) {
        TextView selectedIndex = (TextView) view;
        mProfileListView.setSelection(mIndexToPos.get(selectedIndex.getText()));
    }

    private void fetchSharedProfiles() {
        mProgressDialog.show();

        new AsyncTask<Void, Void, List<ParseUser>>() {
            @Override
            protected List<ParseUser> doInBackground(Void... voids) {
                // query profiles that are to be shared
                return new ArrayList<ParseUser>();
            }

            @Override
            protected void onPostExecute(List<ParseUser> parseUsers) {
                mListAdapter = new ProfileListAdapter(NetworkingActivity.this, parseUsers);
                mProfileListView.setAdapter(mListAdapter);
                updateSideIndex();
                mProgressDialog.dismiss();
            }
        }.execute();
    }

    private void updateSideIndex() {
        // get all users with different first character
        List<ParseUser> users = mListAdapter.getUsers();
        for (int i = 0; i < users.size(); i++) {
            String fullName = users.get(i).getString("full_name");
            String index = fullName.substring(0, 1).toUpperCase();

            if (!mIndexToPos.containsKey(index)) {
                mIndexToPos.put(index, i);
            }
        }

        // update the side index bar view
        mIndexSideBar.removeAllViews();
        TextView textView;
        List<String> indexList = new ArrayList<String>(mIndexToPos.keySet());
        for (String index : indexList) {
            textView = (TextView) getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(this);
            mIndexSideBar.addView(textView);
        }

    }
}