package cmu.cconfs.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.List;

import cmu.cconfs.R;
import cmu.cconfs.model.parseModel.Profile;

/**
 * Created by qiuzhexin on 1/13/17.
 */

public class ProfileListAdapter extends BaseAdapter {
    private Context mContext;
    private List<ParseUser> mUsers;

    public ProfileListAdapter(Context c, List<ParseUser> users) {
        mContext = c;
        mUsers = users;
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = View.inflate(viewGroup.getContext(), R.layout.item_list_profile, null);
            holder = new ViewHolder();
            holder.mNameTv = (TextView) view.findViewById(R.id.profile_name);
            holder.mEmailTv = (TextView) view.findViewById(R.id.profile_email);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.mNameTv.setText(mUsers.get(i).getString(Profile.FULL_NAME_KEY));
        holder.mEmailTv.setText(mUsers.get(i).getEmail());

        return view;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return mUsers.get(i);
    }

    public List<ParseUser> getUsers() {
        return mUsers;
    }

    class ViewHolder {
        TextView mNameTv;
        TextView mEmailTv;
    }


}
