package cmu.cconfs;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cmu.cconfs.adapter.ModeratorPaperResultAdapter;
import cmu.cconfs.adapter.ModeratorResultAdapter;
import cmu.cconfs.model.parseModel.Paper;
import cmu.cconfs.model.parseModel.Session_Timeslot;
import cmu.cconfs.model.parseModel.Timeslot;

public class ModeratorResultActivity extends AppCompatActivity {
    public final static String EXTRA_SESSION_IDS = "extra-session-ids";
    private final static String TAG = ModeratorResultActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private RecyclerView mSessionRecyclerView;
    private ProgressDialog mProgressDialog;
    private ModeratorResultAdapter mModeratorResultAdapter;

    private ModeratorPaperResultAdapter mModeratorPaperResultAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderator);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);

        mSessionRecyclerView = (RecyclerView) findViewById(R.id.main_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mSessionRecyclerView.setLayoutManager(linearLayoutManager);

        mModeratorResultAdapter = new ModeratorResultAdapter(ModeratorResultActivity.this, new ArrayList<ModeratorResultAdapter.Session>());
        mModeratorPaperResultAdapter = new ModeratorPaperResultAdapter(ModeratorResultActivity.this, new ArrayList<Paper>());

        mSessionRecyclerView.setAdapter(mModeratorPaperResultAdapter);

        if (getIntent().hasExtra(EXTRA_SESSION_IDS)) {
//            loadSessions(getIntent().getStringExtra(EXTRA_SESSION_IDS));
            loadPapers(getIntent().getStringExtra(EXTRA_SESSION_IDS));
        }

    }

    private void loadSessions(String ids) {
        mProgressDialog.show();

        new AsyncTask<String, Void, List<ModeratorResultAdapter.Session>>() {
            @Override
            protected List<ModeratorResultAdapter.Session> doInBackground(String... ids) {
                try {
                    ParseQuery<Session_Timeslot> query = Session_Timeslot.getQuery().fromLocalDatastore();
                    // get corresponding session ids
                    ArrayList<Integer> sessionIds = new ArrayList<Integer>();
                    for (String id : ids[0].split(",")) {
                        sessionIds.add(Integer.parseInt(id));
                    }
                    query.whereContainedIn("session_id", sessionIds);
                    List<Session_Timeslot> sessions_t = query.find();
                    List<ModeratorResultAdapter.Session> sessions = new ArrayList<ModeratorResultAdapter.Session>();
                    for (Session_Timeslot st : sessions_t) {
                        // find session timeslot
                        ParseQuery<Timeslot> query2 = Timeslot.getQuery().fromLocalDatastore();
                        query2.whereEqualTo("timeslot_id", st.getSessionId());
                        Timeslot timeslot = query2.getFirst();
                        // construct pojo for adapter
                        ModeratorResultAdapter.Session session = new ModeratorResultAdapter.Session();
                        session.mPapers = st.getPapers();
                        session.mSessionChair = st.getChair();
                        session.mSessionName = st.getValue();
                        session.mSessionRoom = st.getRoom();
                        session.mSessionTime = timeslot.getValue();
                        session.mSessionTitle = st.getSessionTitle();
                        sessions.add(session);
                    }
                    return sessions;
                } catch (ParseException e) {
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<ModeratorResultAdapter.Session> sessions) {
                if (sessions != null) {
                    mModeratorResultAdapter.setSessions(sessions);
                    mModeratorResultAdapter.notifyDataSetChanged();
                }
                mProgressDialog.dismiss();
            }
        }.execute(ids);
    }

    private void loadPapers(final String ids) {
        mProgressDialog.show();
        new AsyncTask<String, Void, List<Paper>>() {
            @Override
            protected List<Paper> doInBackground(String... strings) {
                try {
                    ParseQuery<Paper> query = Paper.getQuery().fromLocalDatastore();
                    // get corresponding paper ids
                    List<String> uids = Arrays.asList(ids.split(","));
                    query.whereContainedIn("unique_id", uids);
                    List<Paper> papers = query.find();
                    return papers;
                } catch (ParseException e) {
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Paper> papers) {
                if (papers != null) {
                    mModeratorPaperResultAdapter.setPapers(papers);
                    mModeratorPaperResultAdapter.notifyDataSetChanged();
                }
                mProgressDialog.dismiss();
            }
        }.execute(ids);
    }

}
