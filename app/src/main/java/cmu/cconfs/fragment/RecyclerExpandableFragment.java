package cmu.cconfs.fragment;

import android.app.Activity;
import android.graphics.drawable.NinePatchDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.parse.Parse;
import com.parse.ParseException;

import cmu.cconfs.CConfsApplication;
import cmu.cconfs.R;
import cmu.cconfs.adapter.ExpandableItemAdapter;
import cmu.cconfs.model.parseModel.Version;
import cmu.cconfs.parseUtils.helper.LoadingUtils;
import cmu.cconfs.utils.data.UnityDataProvider;

/**
 * Created by zmhbh on 7/30/15.
 */
public class RecyclerExpandableFragment extends Fragment {

    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";
    private static final String TAG = RecyclerExpandableFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Handler mHandler;
    private Thread mRefreshTask;

    private int dateIndex;


    public static RecyclerExpandableFragment newInstance() {
        RecyclerExpandableFragment newInstance = new RecyclerExpandableFragment();
        return newInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_recycler_expandable_view, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //access the dateIndex data
        dateIndex = getArguments().getInt("dateIndex");


        super.onViewCreated(view, savedInstanceState);
        //noinspection ConstantConditions
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);

//        // set the refresh action
//        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh_layout);
//        mSwipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.green, R.color.blue, R.color.yellow);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Toast.makeText(getContext(), "Refresh the content!", Toast.LENGTH_LONG).show();
//                mRefreshTask.start();
//            }
//        });
//
//        // define handler and refresh task
//        mHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                mSwipeRefreshLayout.setRefreshing(false);
//                Activity activity = getActivity();
//                if (activity != null) {
//                    activity.recreate();
//                }
//            }
//        };
//        mRefreshTask = new Thread(new RefreshRunnable());

        mLayoutManager = new LinearLayoutManager(getActivity());

        final Parcelable eimSavedState = (savedInstanceState != null) ? savedInstanceState.getParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER) : null;
        mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(eimSavedState);

        //adapter

        final ExpandableItemAdapter myItemAdapter = new ExpandableItemAdapter(getUnityDataProvider(dateIndex));

        mWrappedAdapter = mRecyclerViewExpandableItemManager.createWrappedAdapter(myItemAdapter);       // wrap for expanding

        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();
        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Need to disable them when using animation indicator.
        animator.setSupportsChangeAnimations(false);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.setHasFixedSize(false);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            mRecyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z1)));
        }
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));

        mRecyclerViewExpandableItemManager.attachRecyclerView(mRecyclerView);

        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save current state to support screen rotation, etc...
        if (mRecyclerViewExpandableItemManager != null) {
            outState.putParcelable(
                    SAVED_STATE_EXPANDABLE_ITEM_MANAGER,
                    mRecyclerViewExpandableItemManager.getSavedState());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (mRecyclerViewExpandableItemManager != null) {
            mRecyclerViewExpandableItemManager.release();
            mRecyclerViewExpandableItemManager = null;
        }

        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mLayoutManager = null;

        super.onDestroyView();
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    public UnityDataProvider getUnityDataProvider(int dateIndex) {

        return ((CConfsApplication) getActivity().getApplication()).getUnityDataProvider(dateIndex);
    }

    private boolean isUpToDate() {
        try {
            String local = Version.getQuery().fromLocalDatastore().getFirst().getVersion();
            String remote = Version.getQuery().getFirst().getVersion();
            Log.d(TAG, "Local parse version: " + local);
            Log.d(TAG, "Remote parse version: " + remote);
            return local.equals(remote);
        } catch (ParseException e) {
            Log.e(TAG, "Error check db version: "  + e.getMessage());
        }
        return false;
    }

    private class RefreshRunnable implements Runnable {
        @Override
        public void run() {
            try {
                LoadingUtils.loadFromParse();
                LoadingUtils.populateDataProvider();
                LoadingUtils.populateRoomProvider();
                mHandler.sendEmptyMessage(1);
            } catch (ParseException e) {
                Log.e(TAG, "Error reloading data: " + e.getMessage());
            }
        }
    }

}
