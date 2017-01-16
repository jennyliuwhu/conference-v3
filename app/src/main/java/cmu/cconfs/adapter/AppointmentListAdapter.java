package cmu.cconfs.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;

import java.util.List;

import cmu.cconfs.R;
import cmu.cconfs.model.parseModel.Appointment;
import cmu.cconfs.model.parseModel.Profile;
import cmu.cconfs.parseUtils.helper.CloudCodeUtils;

/**
 * Created by qiuzhexin on 1/15/17.
 */

public class AppointmentListAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private List<Appointment> mAppointments;

    public AppointmentListAdapter(Context context, List<Appointment> appointments) {
        mContext = context;
        mAppointments = appointments;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.swipe_list_item, null);
        final SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
        v.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "click delete at " + position, Toast.LENGTH_SHORT).show();
                // delete both appoints in the remote backend
                Appointment appointment = mAppointments.get(position);
                String otherUsername = appointment.getOtherUsername();
                appointment.deleteInBackground();
                ParseQuery<Appointment> query = Appointment.getQuery();
                query.whereEqualTo(Appointment.MY_USERNAME_KEY, otherUsername);
                query.getFirstInBackground(new GetCallback<Appointment>() {
                    @Override
                    public void done(Appointment object, ParseException e) {
                        object.deleteInBackground();
                    }
                });
                swipeLayout.close();
                mAppointments.remove(position);
                notifyDataSetChanged();
                // send notification to the other about the cancel
                String title = String.format("Appointment canceled by %s.", ParseUser.getCurrentUser().getString(Profile.FULL_NAME_KEY));
                String brief = String.format("Subject: %s, Time: %s", appointment.getSubject(), appointment.getTime());
                CloudCodeUtils.sendNotification(title, brief, otherUsername, CloudCodeUtils.APPOINTMENT_CANCEL_MSG_TYPE);
                Log.d("TAG", otherUsername);
            }
        });
        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        TextView title = (TextView)convertView.findViewById(R.id.title_text);
        TextView brief = (TextView)convertView.findViewById(R.id.brief_text);
        title.setText("Appointment with " + mAppointments.get(position).getOtherRealName());
        brief.setText("Subject: " + mAppointments.get(position).getSubject());
    }

    @Override
    public int getCount() {
        return mAppointments.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return mAppointments.get(position);
    }

    public Appointment getAppointment(int i) {
        return mAppointments.get(i);
    }
}
