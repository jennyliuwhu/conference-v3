package cmu.cconfs.adapter;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cmu.cconfs.R;

/**
 * Created by qiuzhexin on 12/21/16.
 */

public class PlaceTypeAdapter extends BaseAdapter {
    private static String[] types = { "Pick One", "Attractions", "Convenience", "Emergency", "Food", "Shopping", "Transportation" };
    private static String[] supportedTypes = { "", "amusement_park", "store", "hospital", "restaurant", "shopping_mall", "bus_station" };
    private static int[] imgResIds = { R.drawable.ic_near_me_black_48dp, R.drawable.ic_landscape_black_48dp, R.drawable.ic_local_convenience_store_black_48dp, R.drawable.ic_local_hospital_black_48dp, R.drawable.ic_restaurant_black_48dp, R.drawable.ic_shopping_basket_black_48dp, R.drawable.ic_drive_eta_black_48dp  };
    private List<Pair<String, Integer>> mPlaceTypePairs;


    public PlaceTypeAdapter(Context c) {
        mPlaceTypePairs = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            mPlaceTypePairs.add(new Pair<String, Integer>(types[i], imgResIds[i]));

        }
    }

    @Override
    public int getCount() {
        return mPlaceTypePairs.size();
    }

    @Override
    public Object getItem(int position) {
        return mPlaceTypePairs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            view = View.inflate(parent.getContext(), R.layout.item_list_place, null);
            holder = new ViewHolder();
            holder.mPlaceTypeTextView = (TextView) view.findViewById(R.id.place_type_tv);
            holder.mThumbnail = (ImageView) view.findViewById(R.id.list_image);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.mPlaceTypeTextView.setText(mPlaceTypePairs.get(position).first);
        holder.mThumbnail.setImageResource(mPlaceTypePairs.get(position).second);

        return view;
    }

    public static String[] getTypes() {
        return types;
    }

    public static String getSupportedSearchType(int position) {
        return supportedTypes[position];
    }

    private class ViewHolder {
        ImageView mThumbnail;
        TextView mPlaceTypeTextView;
    }
}
