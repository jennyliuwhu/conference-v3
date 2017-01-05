package cmu.cconfs;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cmu.cconfs.adapter.ModeratorAdapter;
import cmu.cconfs.model.parseModel.AuthorSession;
import cmu.cconfs.utils.motion.EndlessRecyclerViewScrollListener;

public class ModeratorActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mModeratorRecyclerView;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private ModeratorAdapter mModeratorAdapter;

    private Handler mHandler;

    int TOTAL_MODERATOR_ITEMS;
    int MODERATOR_ITEMS_IN_ONE_PAGE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderator);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mHandler = new Handler();
        
        mModeratorRecyclerView = (RecyclerView) findViewById(R.id.moderator_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mModeratorRecyclerView.setLayoutManager(linearLayoutManager);

        mScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };

        mModeratorRecyclerView.addOnScrollListener(mScrollListener);

        List<AuthorSession> moderators = new ArrayList<>();
        moderators.add(new AuthorSession());
        mModeratorAdapter = new ModeratorAdapter(getApplicationContext(), moderators);

        mModeratorRecyclerView.setAdapter(mModeratorAdapter);

    }

    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted();

    }
}
