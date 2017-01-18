package cmu.cconfs;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cmu.cconfs.adapter.AppointmentListAdapter;
import cmu.cconfs.fragment.AppointmentDetailFragment;
import cmu.cconfs.model.parseModel.Appointment;

public class AppointmentListActivity extends AppCompatActivity {
    private final static String TAG = AppointmentListActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private ListView mListView;
    private AppointmentListAdapter mAdapter;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Appointment");


        mListView = (ListView) findViewById(R.id.swipe_list_view);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(view.getContext(), "item " + position + " clicked.", Toast.LENGTH_SHORT).show();
                AppointmentDetailFragment fragment
                        = AppointmentDetailFragment.newInstance(
                        mAdapter.getAppointment(position).getSubject(),
                        mAdapter.getAppointment(position).getDetail());
                fragment.show(getSupportFragmentManager(), "appt-detail");
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ((SwipeLayout)(mListView.getChildAt(position - mListView.getFirstVisiblePosition()))).open(true);
                return true;
            }
        });

        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);

        fetchAppointments();
    }

    private void fetchAppointments() {
        mProgressDialog.show();

        new AsyncTask<Void, Void, List<Appointment>>() {
            @Override
            protected List<Appointment> doInBackground(Void... voids) {
                List<Appointment> appointments = new ArrayList<Appointment>();
                try {
                    ParseQuery<Appointment> query = Appointment.getQuery();
                    query.whereEqualTo(Appointment.MY_USERNAME_KEY, ParseUser.getCurrentUser().getUsername());
                    appointments = query.find();
                    sortAppointmentList(appointments);
                } catch (ParseException e) {
                    Log.e(TAG, e.getMessage());
                }
                return appointments;
            }

            @Override
            protected void onPostExecute(List<Appointment> appointments) {
                mAdapter = new AppointmentListAdapter(AppointmentListActivity.this, appointments);
                mListView.setAdapter(mAdapter);
                mAdapter.setMode(Attributes.Mode.Single);
                mProgressDialog.dismiss();
            }

            // sort appointment by start time
            private void sortAppointmentList(List<Appointment> appointments) {
                Collections.sort(appointments, new Comparator<Appointment>() {
                    @Override
                    public int compare(Appointment app1, Appointment app2) {
                        Date date1 = getAppointmentDate(app1);
                        Date date2 = getAppointmentDate(app2);

                        return date1 == null || date2 == null ? 0 : date1.compareTo(date2);
                    }
                });
            }

            private Date getAppointmentDate(Appointment appointment) {
                String time = appointment.getTime();
                String startHourMinute = time.split(",")[0].split("-")[0];
                String dateStr = time.split(",")[1];
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm");

                try {
                    Date date = sdf.parse(dateStr + " " + startHourMinute);
                    return date;
                } catch (java.text.ParseException e) {
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }
        }.execute();
    }
}
