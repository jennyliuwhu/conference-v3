package cmu.cconfs.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cmu.cconfs.R;
import cmu.cconfs.SessionActivity;

/**
 * Created by qiuzhexin on 1/6/17.
 */

public class ModeratorResultAdapter extends RecyclerView.Adapter<ModeratorResultAdapter.ViewHolder> {
    Context mContext;
    List<Session> mSessions;

    public ModeratorResultAdapter(Context c, List<Session> sessions) {
        mContext = c;
        mSessions = sessions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mSessionNameTv.setText(mSessions.get(position).mSessionName);
    }

    @Override
    public int getItemCount() {
        return mSessions.size();
    }

    public void setSessions(List<Session> sessions) {
        mSessions = sessions;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mSessionNameTv;

        public ViewHolder(View viewItem) {
            super(viewItem);
            itemView.setClickable(true);
            itemView.setFocusableInTouchMode(true);
            itemView.setFocusable(true);
            itemView.setOnClickListener(this);

            mSessionNameTv = (TextView) itemView.findViewById(R.id.author_name);
        }

        @Override
        public void onClick(View view) {
            // transit to the corresponding session
            Session session = mSessions.get(getAdapterPosition());
            Intent i = new Intent(view.getContext(), SessionActivity.class);
            i.putExtra("papers", session.mPapers);
            i.putExtra("sessionTime", session.mSessionTime);
            i.putExtra("sessionName", session.mSessionTitle);
            i.putExtra("sessionRoom", session.mSessionRoom);
            i.putExtra("sessionChair", session.mSessionChair);
            view.getContext().startActivity(i);
        }
    }

    public static class Session {
        public String mPapers;
        public String mSessionTime;
        public String mSessionName;
        public String mSessionChair;
        public String mSessionRoom;
        public String mSessionTitle;

        public Session() {

        }
    }
}
