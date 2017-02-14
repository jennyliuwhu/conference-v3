package cmu.cconfs;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.xgc1986.parallaxPagerTransformer.ParallaxPagerTransformer;

import java.util.List;

import cmu.cconfs.adapter.FloorGuideAdapter;
import cmu.cconfs.fragment.FloorGuideFragment;
import cmu.cconfs.model.parseModel.FloorPlan;


public class FloorGuideActivity extends FragmentActivity {

    ViewPager mPager;
    FloorGuideAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_guide);

        System.out.println("Hey there");
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setBackgroundColor(0xFF000000);

        ParallaxPagerTransformer pt = new ParallaxPagerTransformer(R.id.image);
        pt.setBorder(20);
        // pt.setSpeed(0.8f);

        mPager.setPageTransformer(false, pt);

        mAdapter = new FloorGuideAdapter(getSupportFragmentManager());
        mAdapter.setPager(mPager);

        ParseQuery<FloorPlan> query = FloorPlan.getQuery();
        query.fromLocalDatastore();
        query.fromPin(FloorPlan.PIN_TAG);
        List<FloorPlan> floorPlans = null;
        try {
            floorPlans = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (FloorPlan floorPlan : floorPlans) {
            try {
                ParseFile parseFile = floorPlan.getPhotoFile();
                byte[] response = parseFile.getData();

                Bundle bundle = new Bundle();
                bundle.putByteArray("image", response);
                bundle.putString("name", floorPlan.getPhototName());
                FloorGuideFragment floorFragment = new FloorGuideFragment();
                floorFragment.setArguments(bundle);
                mAdapter.add(floorFragment);
            } catch (Exception e) {
                Log.e("photo", "Error: " + e.getMessage());
            }
        }

        mPager.setAdapter(mAdapter);
        System.out.println("It reaches here!");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();

        return super.onOptionsItemSelected(item);
    }
}
