package cmu.cconfs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.HashMap;

import cmu.cconfs.fragment.SendMessageFragment;
import cmu.cconfs.model.parseModel.Appointment;
import cmu.cconfs.model.parseModel.Profile;
import cmu.cconfs.parseUtils.helper.CloudCodeUtils;

public class NotificationDetailActivity extends AppCompatActivity {
    private final static String TAG = NotificationActivity.class.getSimpleName();
    public final static String EXTRA_NOTI_DATA = "data";

    private TextView mDetailTypeTv;
    private TextView mTitleTv;
    private TextView mDetailTv;
    private Button mAcceptBtn;
    private Button mRejectBtn;

    private Button mReplyBtn;

    private AppointmentActivity.NotificationPayload mAppointmentNotificationPayload;
    private SendMessageFragment.NotificationPayload mSendMessageNotificationPayload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_result);

        mDetailTypeTv = (TextView) findViewById(R.id.detail_type_label);

        mTitleTv = (TextView) findViewById(R.id.noti_title_tv);
        mDetailTv = (TextView) findViewById(R.id.noti_detail_tv);
        mAcceptBtn = (Button) findViewById(R.id.acc_btn);
        mRejectBtn = (Button) findViewById(R.id.rej_btn);
        mReplyBtn = (Button) findViewById(R.id.reply_btn);

        mAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save appointments for both users
                String myUsername = ParseUser.getCurrentUser().getUsername();
                String myRealName = ParseUser.getCurrentUser().getString(Profile.FULL_NAME_KEY);
                String otherUsername = mAppointmentNotificationPayload.getSenderUsername();
                String otherRealName = mAppointmentNotificationPayload.getSenderRealName();
                String subject = mAppointmentNotificationPayload.getSubject();
                String startTime = mAppointmentNotificationPayload.getStartTime();
                String endTime = mAppointmentNotificationPayload.getEndTime();
                String date = mAppointmentNotificationPayload.getDate();
                String time = startTime + "-" + endTime + ", " + date;
                String detail = mAppointmentNotificationPayload.toString();

                Appointment appointment = new Appointment();
                appointment.setMyUsername(myUsername);
                appointment.setOtherUsername(otherUsername);
                appointment.setOtherRealName(otherRealName);
                appointment.setSubject(subject);
                appointment.setTime(time);
                appointment.setDetail(detail);
                appointment.saveEventually();

                appointment = new Appointment();
                appointment.setMyUsername(otherUsername);
                appointment.setOtherUsername(myUsername);
                appointment.setOtherRealName(myRealName);
                appointment.setSubject(subject);
                appointment.setTime(time);
                appointment.setDetail(detail);
                appointment.saveEventually();

                // notify sender about the acceptance
                CloudCodeUtils.sendNotification("Appointment with " + ParseUser.getCurrentUser().getString(Profile.FULL_NAME_KEY),
                        "Status: accepted",
                        mAppointmentNotificationPayload.getSenderUsername(),
                        CloudCodeUtils.APPOINTMENT_ACCEPT_MSG_TYPE);
                finish();
            }
        });
        mRejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // notify sender about the rejection
                CloudCodeUtils.sendNotification("Appointment with " + ParseUser.getCurrentUser().getString(Profile.FULL_NAME_KEY),
                        "Status: rejected",
                        mAppointmentNotificationPayload.getSenderUsername(),
                        CloudCodeUtils.APPOINTMENT_REJECT_MSG_TYPE);
                finish();

            }
        });
        mReplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send message back to the other user
                SendMessageFragment fragment = SendMessageFragment.newInstance(mSendMessageNotificationPayload.getSenderUsername());
                fragment.show(getSupportFragmentManager(), "msg-frag");
            }
        });

        HashMap<String, String> data = (HashMap<String, String>) getIntent().getSerializableExtra(EXTRA_NOTI_DATA);
        Log.d(TAG, "Get intent extra: " + data);

        String msgType = data.get("type");
        switch (msgType) {
            case CloudCodeUtils.APPOINTMENT_REQUEST_MSG_TYPE:
                mAppointmentNotificationPayload = AppointmentActivity.NotificationPayload.fromJsonStr(data.get("body"));
                mDetailTypeTv.setText("Appointment");
                mTitleTv.setText(mAppointmentNotificationPayload.getSubject());
                mDetailTv.setText(mAppointmentNotificationPayload.toString());
                break;
            case CloudCodeUtils.NORMAL_MESSAGE_MSG_TYPE:
                mSendMessageNotificationPayload = SendMessageFragment.NotificationPayload.fromJsonStr(data.get("body"));
                mDetailTypeTv.setText("Message from " + mSendMessageNotificationPayload.getSenderRealName());
                mTitleTv.setText(mSendMessageNotificationPayload.getTitle());
                mDetailTv.setText(mSendMessageNotificationPayload.getMessage());

                mAcceptBtn.setVisibility(View.GONE);
                mRejectBtn.setVisibility(View.GONE);
                mReplyBtn.setVisibility(View.VISIBLE);
                break;
        }
    }

}
