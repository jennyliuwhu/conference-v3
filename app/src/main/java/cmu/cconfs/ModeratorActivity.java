package cmu.cconfs;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.android.gms.auth.api.Auth;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cmu.cconfs.adapter.ModeratorAdapter;
import cmu.cconfs.model.parseModel.AuthorSession;
import cmu.cconfs.utils.motion.EndlessRecyclerViewScrollListener;

public class ModeratorActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private final static String TAG = ModeratorActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private RecyclerView mModeratorRecyclerView;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private ModeratorAdapter mModeratorAdapter;

    private Handler mHandler;

    private ProgressDialog mProgressDialog;

    int TOTAL_MODERATOR_ITEMS;
    int MODERATOR_ITEMS_IN_ONE_PAGE = 50;
    String mNameQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderator);

        initialize();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mHandler = new Handler();

        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);

        mModeratorRecyclerView = (RecyclerView) findViewById(R.id.main_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mModeratorRecyclerView.setLayoutManager(linearLayoutManager);

        mScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount >= TOTAL_MODERATOR_ITEMS) {
                    return;
                }
                loadAuthorPage(totalItemsCount);
            }
        };

        loadFirstAuthorPage();
    }

    // load the first page
    private void loadFirstAuthorPage() {
        mProgressDialog.show();

        new AsyncTask<Void, Void, List<AuthorSession>>() {
            @Override
            protected List<AuthorSession> doInBackground(Void... voids) {
                return getAuthorPage(0, mNameQuery);
            }

            @Override
            protected void onPostExecute(List<AuthorSession> authorSessions) {
                mModeratorRecyclerView.addOnScrollListener(mScrollListener);
                mScrollListener.resetState();
                mModeratorAdapter = new ModeratorAdapter(getApplicationContext(), authorSessions);
                mModeratorRecyclerView.setAdapter(mModeratorAdapter);
                mProgressDialog.dismiss();
            }
        }.execute();
    }

    private void loadAuthorPage(final int itemCount) {
        mProgressDialog.show();

        new AsyncTask<Void, Void, List<AuthorSession>>() {
            @Override
            protected List<AuthorSession> doInBackground(Void... voids) {
                return getAuthorPage(itemCount, mNameQuery);
            }

            @Override
            protected void onPostExecute(List<AuthorSession> authorSessions) {
                mModeratorAdapter.appendMore(authorSessions);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mModeratorAdapter.notifyDataSetChanged();
                    }
                });
                mProgressDialog.dismiss();
            }
        }.execute();
    }

    private List<AuthorSession> getAuthorPage(int itemCount, String nameQuery) {
        List<AuthorSession> res = new ArrayList<>();
        try {
            ParseQuery<AuthorSession> query = ParseQuery.getQuery(AuthorSession.class).fromLocalDatastore();
            query.orderByAscending("author");
//            query.whereGreaterThanOrEqualTo("author_id", page * MODERATOR_ITEMS_IN_ONE_PAGE);
//            query.whereLessThan("author_id", (page + 1) * MODERATOR_ITEMS_IN_ONE_PAGE);
            List<AuthorSession> filtered = new ArrayList<>();
            for (AuthorSession as : query.find()) {
                String author = as.getAuthor().toLowerCase();
                nameQuery = nameQuery.toLowerCase();
                if (author.startsWith(nameQuery) || author.endsWith(nameQuery)) {
                    filtered.add(as);
                }
            }

            // update total items for the query
            TOTAL_MODERATOR_ITEMS = filtered.size();
            for (int i = itemCount == 0 ? 0 : itemCount - 1; res.size() < MODERATOR_ITEMS_IN_ONE_PAGE && i < filtered.size(); i++) {
                res.add(filtered.get(i));
            }
        } catch (ParseException e) {
            Log.d(TAG, "Error getting author page: " + e.getMessage());
        }
        Log.d(TAG, "Get author page: " + res.toString());
        return res;
    }

    private void initialize() {
        try {
            TOTAL_MODERATOR_ITEMS = AuthorSession.getQuery().fromLocalDatastore().count();
            Log.d(TAG, "Total authors: " + TOTAL_MODERATOR_ITEMS);
        } catch (ParseException e) {
            Log.e(TAG, "Error initialize author component: " + e.getMessage());
        }
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
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mNameQuery = query;
        loadFirstAuthorPage();
        return true;
    }
}
