package cmu.cconfs.adapter;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cmu.cconfs.R;

/**
 * Created by qiuzhexin on 12/21/16.
 */

public class PlaceResultAdapter extends BaseAdapter {
    private Context mContext;
    private List<Pair<String, String>> mPlacePairs;

    public PlaceResultAdapter(Context c, List<Pair<String, String>> pairs) {
        mContext = c;
        mPlacePairs = pairs;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Pair<String, String> getItem(int position) {
        return mPlacePairs.get(position);
    }

    @Override
    public int getCount() {
        return mPlacePairs.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            view = View.inflate(parent.getContext(), R.layout.item_result_list_place, null);
            holder = new ViewHolder();
            holder.mPlaceName = (TextView) view.findViewById(R.id.place_name_tv);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.mPlaceName.setText(mPlacePairs.get(position).second);

        return view;
    }

    public void setPlacePairs(List<Pair<String, String>> pairs) {
        mPlacePairs.clear();
        mPlacePairs.addAll(pairs);
    }

    private class ViewHolder {
        TextView mPlaceName;
    }
}
