package cmu.cconfs.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cmu.cconfs.R;
import cmu.cconfs.adapter.FloorGuideAdapter;


/**
 * A placeholder fragment containing a simple view.
 */
public class FloorGuideFragment extends Fragment {

    private FloorGuideAdapter mCatsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_floor_guide, container, false);
        final ImageView image = (ImageView) v.findViewById(R.id.weatherimg);

        byte[] bytes = getArguments().getByteArray("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        image.setImageBitmap(bitmap);

//        image.post(new Runnable() {
//            @Override
//            public void run() {
//                Matrix matrix = new Matrix();
//                matrix.reset();
//
//                float wv = image.getWidth();
//                float hv = image.getHeight();
//
//                float wi = image.getDrawable().getIntrinsicWidth();
//                float hi = image.getDrawable().getIntrinsicHeight();
//
//                float width = wv;
//                float height = hv;
//
//                if (wi / wv > hi / hv) {
//                    matrix.setScale(hv / hi, hv / hi);
//                    width = wi * hv / hi;
//                } else {
//                    matrix.setScale(wv / wi, wv / wi);
//                    height = hi * wv / wi;
//                }
//
//                matrix.preTranslate((wv - width) / 2, (hv - height - 5) / 2);
//                image.setScaleType(ImageView.ScaleType.MATRIX);
//                image.setImageMatrix(matrix);
//            }
//        });


        TextView text = (TextView) v.findViewById(R.id.name);
        text.setText(getArguments().getString("name"));

        return v;
    }

    public void setAdapter(FloorGuideAdapter catsAdapter) {
        mCatsAdapter = catsAdapter;
    }
}
