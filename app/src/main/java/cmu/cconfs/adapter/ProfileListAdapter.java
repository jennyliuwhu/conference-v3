package cmu.cconfs.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import cmu.cconfs.R;
import cmu.cconfs.model.parseModel.Profile;

/**
 * Created by qiuzhexin on 1/13/17.
 */

public class ProfileListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Profile> mProfiles;
    private List<Profile> mFilteredProfiles;

    public ProfileListAdapter(Context c, List<Profile> profiles) {
        mContext = c;
        mProfiles = profiles;
        mFilteredProfiles = mProfiles;
    }

    @Override
    public int getCount() {
        return mFilteredProfiles.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = View.inflate(viewGroup.getContext(), R.layout.item_list_profile, null);
            holder = new ViewHolder();
            holder.mNameTv = (TextView) view.findViewById(R.id.profile_name);
            holder.mOrganizationTv = (TextView) view.findViewById(R.id.profile_organization);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.mNameTv.setText(mFilteredProfiles.get(i).getFullName());
        holder.mOrganizationTv.setText(mFilteredProfiles.get(i).getCompany());

        return view;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return mFilteredProfiles.get(i);
    }

    public List<Profile> getProfiles() {
        return mFilteredProfiles;
    }

    public void filter(String query) {
        mFilteredProfiles = new ArrayList<>();
        for (int i = 0; i < mProfiles.size(); i++) {
            Profile profile = mProfiles.get(i);
            if (profile.getFullName().toLowerCase().startsWith(query.toLowerCase()) || profile.getFullName().toLowerCase().endsWith(query.toLowerCase())) {
                mFilteredProfiles.add(profile);
            }
        }
    }

    class ViewHolder {
        TextView mNameTv;
        TextView mOrganizationTv;
    }


}
