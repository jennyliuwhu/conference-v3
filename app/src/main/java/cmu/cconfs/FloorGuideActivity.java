package cmu.cconfs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * todo change to viewPager
 * http://www.viralandroid.com/2016/04/android-image-slider-tutorial.html
 * http://www.androhub.com/android-image-slider-using-viewpager/
 */

public class FloorGuideActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_guide);

//        System.out.println("Hey there");
//        mPager = (ViewPager) findViewById(R.id.pager);
//        mPager.setBackgroundColor(0xFF000000);
//
//        ParallaxPagerTransformer pt = new ParallaxPagerTransformer(R.id.weatherimg);
//        pt.setBorder(20);
//        // pt.setSpeed(0.8f);
//
//        mPager.setPageTransformer(false, pt);
//
//        mAdapter = new FloorGuideAdapter(getSupportFragmentManager());
//        mAdapter.setPager(mPager);
//
//        ParseQuery<FloorPlan> query = FloorPlan.getQuery();
//        query.fromLocalDatastore();
//        query.fromPin(FloorPlan.PIN_TAG);
//        List<FloorPlan> floorPlans = null;
//        try {
//            floorPlans = query.find();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        for (FloorPlan floorPlan : floorPlans) {
//            try {
//                ParseFile parseFile = floorPlan.getPhotoFile();
//                byte[] response = parseFile.getData();
//
//                Bundle bundle = new Bundle();
//                bundle.putByteArray("image", response);
//                bundle.putString("name", floorPlan.getPhototName());
//                FloorGuideFragment floorFragment = new FloorGuideFragment();
//                floorFragment.setArguments(bundle);
//                mAdapter.add(floorFragment);
//            } catch (Exception e) {
//                Log.e("photo", "Error: " + e.getMessage());
//            }
//        }
//
//        mPager.setAdapter(mAdapter);
//        System.out.println("It reaches here!");
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPageAndroid);
        AndroidImageAdapter adapterView = new AndroidImageAdapter(this);
        mViewPager.setAdapter(adapterView);
    }
}

class AndroidImageAdapter extends PagerAdapter {
    Context mContext;

    AndroidImageAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return sliderImagesId.length;
    }

    private int[] sliderImagesId = new int[] {
            R.drawable.leve_one, R.drawable.level_two, R.drawable.level_three, R.drawable.level_four,
            R.drawable.leve_one, R.drawable.level_two, R.drawable.level_three, R.drawable.level_four,
    };

    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == ((ImageView) obj);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        ImageView mImageView = new ImageView(mContext);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.setImageResource(sliderImagesId[i]);
        ((ViewPager) container).addView(mImageView, 0);
        return mImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        ((ViewPager) container).removeView((ImageView) obj);
    }
}