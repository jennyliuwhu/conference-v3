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
import com.parse.ParseException;

import cmu.cconfs.CConfsApplication;
import cmu.cconfs.R;
import cmu.cconfs.adapter.ExpandableItemAdapter;
import cmu.cconfs.adapter.ExpandableRoomAdapter;
import cmu.cconfs.parseUtils.helper.LoadingUtils;
import cmu.cconfs.utils.data.RoomDataProvider;
import cmu.cconfs.utils.data.UnityDataProvider;


public class RecyclerRoomFragment extends Fragment {
    private static final String TAG = RecyclerRoomFragment.class.getSimpleName();

    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewRoomItemManager";

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;
    private int roomIndex;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Handler mHandler;
    private Thread mRefreshTask;


    public static RecyclerRoomFragment newInstance() {


        RecyclerRoomFragment newInstance = new RecyclerRoomFragment();

        return newInstance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_expandable_view, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //access the dateIndex data
        roomIndex = getArguments().getInt("roomIndex");

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

        final ExpandableRoomAdapter myItemAdapter = new ExpandableRoomAdapter(getRoomDataProvider(roomIndex));

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

    public RoomDataProvider getRoomDataProvider(int dateIndex) {

        return ((CConfsApplication) getActivity().getApplication()).getRoomDataProvider(dateIndex);
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
