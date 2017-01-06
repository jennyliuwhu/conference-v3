package cmu.cconfs;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cmu.cconfs.adapter.ModeratorAdapter;
import cmu.cconfs.model.parseModel.AuthorSession;
import cmu.cconfs.utils.motion.EndlessRecyclerViewScrollListener;

public class ModeratorActivity extends AppCompatActivity {
    private final static String TAG = ModeratorActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private RecyclerView mModeratorRecyclerView;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private ModeratorAdapter mModeratorAdapter;

    private Handler mHandler;

    private ProgressDialog mProgressDialog;

    int TOTAL_MODERATOR_ITEMS;
    int MODERATOR_ITEMS_IN_ONE_PAGE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderator);

        initialize();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mHandler = new Handler();

        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setMessage(getString(R.string.Is_the_registered));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        
        mModeratorRecyclerView = (RecyclerView) findViewById(R.id.moderator_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mModeratorRecyclerView.setLayoutManager(linearLayoutManager);

        mScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount == TOTAL_MODERATOR_ITEMS) {
                    return;
                }
                loadAuthorPage(page);
            }
        };

        mModeratorRecyclerView.addOnScrollListener(mScrollListener);

        List<AuthorSession> moderators = getAuthorPage(0);
        mModeratorAdapter = new ModeratorAdapter(getApplicationContext(), moderators);

        mModeratorRecyclerView.setAdapter(mModeratorAdapter);

    }

    private void loadAuthorPage(final int page) {
        mProgressDialog.show();

        new AsyncTask<Void, Void, List<AuthorSession>>() {
            @Override
            protected List<AuthorSession> doInBackground(Void... voids) {
                return getAuthorPage(page);
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

    private List<AuthorSession> getAuthorPage(int page) {
        List<AuthorSession> res = new ArrayList<>();
        try {
            ParseQuery<AuthorSession> query = ParseQuery.getQuery(AuthorSession.class).fromLocalDatastore();
            query.whereGreaterThanOrEqualTo("author_id", page * MODERATOR_ITEMS_IN_ONE_PAGE);
            query.whereLessThan("author_id", (page + 1) * MODERATOR_ITEMS_IN_ONE_PAGE);
            res = query.find();
        } catch (ParseException e) {
            Log.d(TAG, "Error getting author page: " + e.getMessage());
        }
        Log.d(TAG, "Get author page: " + res.toString());
        return res;
    }

    private void initialize() {
        try {
            TOTAL_MODERATOR_ITEMS = AuthorSession.getQuery().fromLocalDatastore().count();
            MODERATOR_ITEMS_IN_ONE_PAGE = 20;
            Log.d(TAG, "Total authors: " + TOTAL_MODERATOR_ITEMS);
        } catch (ParseException e) {
            Log.e(TAG, "Error initialize author component: " + e.getMessage());
        }
    }
}
