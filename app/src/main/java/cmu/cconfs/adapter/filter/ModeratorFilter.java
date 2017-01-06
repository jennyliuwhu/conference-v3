package cmu.cconfs.adapter.filter;

import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cmu.cconfs.adapter.ModeratorAdapter;
import cmu.cconfs.model.parseModel.AuthorSession;

/**
 * Created by qiuzhexin on 1/6/17.
 */

public class ModeratorFilter extends Filter {

    private List<AuthorSession> mModeratorList;
    private List<AuthorSession> mFilteredModeratorList;
    private ModeratorAdapter mModeratorAdapter;

    public ModeratorFilter(List<AuthorSession> moderators, ModeratorAdapter adapter) {
        mModeratorAdapter = adapter;
        mModeratorList = moderators;
        mFilteredModeratorList = new ArrayList<>();
    }

    @Override
    protected FilterResults performFiltering(CharSequence query) {
        mFilteredModeratorList.clear();
        final FilterResults results = new FilterResults();
        for (final AuthorSession as : mModeratorList) {
            if (as.getAuthor().toLowerCase().contains(query.toString().trim().toLowerCase())) {
                mFilteredModeratorList.add(as);
            }
        }

        results.values = mFilteredModeratorList;
        results.count = mFilteredModeratorList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//        mModeratorAdapter.setFilteredList(mFilteredModeratorList);
        mModeratorAdapter.notifyDataSetChanged();
    }
}
