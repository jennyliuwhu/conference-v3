package cmu.cconfs.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.sql.Time;
import java.util.List;

import cmu.cconfs.PaperActivity;
import cmu.cconfs.R;
import cmu.cconfs.SessionActivity;
import cmu.cconfs.model.parseModel.Paper;
import cmu.cconfs.model.parseModel.Program;
import cmu.cconfs.model.parseModel.Session_Timeslot;
import cmu.cconfs.model.parseModel.Timeslot;

/**
 * Created by qiuzhexin on 1/6/17.
 */

public class ModeratorPaperResultAdapter extends RecyclerView.Adapter<ModeratorPaperResultAdapter.ViewHolder> {
    Context mContext;
    List<Paper> mPapers;
    ProgressDialog mProgressDialog;

    public ModeratorPaperResultAdapter(Context c, List<Paper> papers) {
        mContext = c;
        mPapers = papers;

        mProgressDialog = new ProgressDialog(c, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTitleTv.setText(mPapers.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mPapers.size();
    }

    public void setPapers(List<Paper> papers) {
        mPapers = papers;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTitleTv;

        public ViewHolder(View viewItem) {
            super(viewItem);
            itemView.setClickable(true);
            itemView.setFocusableInTouchMode(true);
            itemView.setFocusable(true);
            itemView.setOnClickListener(this);

            mTitleTv = (TextView) itemView.findViewById(R.id.author_name);
        }

        @Override
        public void onClick(View view) {
            // transit to the corresponding paper detail
            mProgressDialog.show();
            new PreparePaperDetailTask().execute(getAdapterPosition());
        }
    }

    class PreparePaperDetailTask extends AsyncTask<Integer, Void, PaperDetailIntentData> {
        @Override
        protected PaperDetailIntentData doInBackground(Integer... pos) {
            Paper paper = mPapers.get(pos[0]);
            String paperId = paper.getUniqueId();
            String paperAbstract = paper.getAbstract();
            String paperAuthor = paper.getAuthorWithAffiliation();
            String paperTitle = paper.getTitle();

            try {
                ParseQuery<Session_Timeslot> sessions = Session_Timeslot.getQuery().fromLocalDatastore();
                for (Session_Timeslot st : sessions.find()) {
                    if (st.getPapers().contains(paperId)) {
                        String papers = st.getPapers();

                        ParseQuery<Timeslot> query = Timeslot.getQuery().fromLocalDatastore();
                        query.whereEqualTo("timeslot_id", st.getSessionId());
                        Timeslot timeslot = query.getFirst();
                        String time = timeslot.getValue();

                        ParseQuery<Program> pQuery = Program.getQuery().fromLocalDatastore();
                        pQuery.whereEqualTo("program_id", timeslot.getProgramId());
                        Program program = pQuery.getFirst();
                        String date = program.getDate();

                        String key = SessionActivity.getSessionKey(new String[] {st.getSessionTitle(), st.getChair(), st.getRoom(), time});
                        return new PaperDetailIntentData(key, paperAbstract, paperAuthor, paperTitle, papers, date);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(PaperDetailIntentData paperDetailIntentData) {
            mProgressDialog.dismiss();

            if (paperDetailIntentData == null) {
                Toast.makeText(mContext, "Something goes wrong T-T", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(mContext, PaperActivity.class);
            i.putExtra("paperTitle", paperDetailIntentData.paperTitle);
            i.putExtra("paperAbstract", paperDetailIntentData.paperAbstract);
            i.putExtra("paperAuthor", paperDetailIntentData.paperAuthor);
            i.putExtra("sessionKey", paperDetailIntentData.sessionKey);
            i.putExtra("papers", paperDetailIntentData.sessionPapers);
            i.putExtra("date", paperDetailIntentData.sessionDate);
            mContext.startActivity(i);
        }
    }

    class PaperDetailIntentData {
        String sessionKey;
        String sessionPapers;
        String sessionDate;
        String paperAbstract;
        String paperAuthor;
        String paperTitle;


        public PaperDetailIntentData(String sessionKey, String paperAbstract, String paperAuthor, String paperTitle, String papers, String date) {
            this.sessionKey = sessionKey;
            this.paperAbstract = paperAbstract;
            this.paperAuthor = paperAuthor;
            this.paperTitle = paperTitle;
            this.sessionPapers = papers;
            this.sessionDate = date;
        }
    }
}
