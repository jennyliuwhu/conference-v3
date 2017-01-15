package cmu.cconfs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.ParcelUuid;
import android.support.design.widget.Snackbar;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseUser;
import com.rey.material.widget.SnackBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import cmu.cconfs.model.parseModel.Profile;
import cmu.cconfs.parseUtils.helper.CloudCodeUtils;

public class AppointmentActivity extends AppCompatActivity {
    private final static String TAG = AppointmentActivity.class.getSimpleName();

    public final static String EXTRA_OHTHER_USERNAME = "username";
    public final static String EXTRA_OHTHER_REAL_NAME = "realname";

    private final static String TIME_FRAG_EXTRA_TYPE = "time-picker";
    private final static int START_TIME_PICKER = 1;
    private final static int END_TIME_PICKER = 2;

    public final static String NOTI_SHORT_DESC = "short-desc";
    public final static String NOTI_DETAIL = "short-desc";

    private TextView mOtherTv;
    private EditText mSubjectEt;
    private Button mDateButton;
    private Button mStartTimeButton;
    private Button mEndTimeButton;
    private EditText mLocationEt;
    private EditText mDescEt;
    private Button mRequestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        mOtherTv = (TextView) findViewById(R.id.other_tv);
        if (getIntent().hasExtra(EXTRA_OHTHER_REAL_NAME)) {
            mOtherTv.setText(getIntent().getStringExtra(EXTRA_OHTHER_REAL_NAME));
        }

        mSubjectEt = (EditText) findViewById(R.id.subject_et);

        mDateButton = (Button) findViewById(R.id.choose_date_btn);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getSupportFragmentManager(), "date-picker");
            }
        });
        mStartTimeButton = (Button) findViewById(R.id.choose_start_time_btn);
        mStartTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment fragment = new TimePickerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(TIME_FRAG_EXTRA_TYPE, START_TIME_PICKER);
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), TIME_FRAG_EXTRA_TYPE);
            }
        });
        mEndTimeButton = (Button) findViewById(R.id.choose_end_time_btn);
        mEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment fragment = new TimePickerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(TIME_FRAG_EXTRA_TYPE, END_TIME_PICKER);
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), TIME_FRAG_EXTRA_TYPE);
            }
        });

        mLocationEt = (EditText) findViewById(R.id.loc_et);
        mDescEt = (EditText) findViewById(R.id.desc_et);

        mRequestButton = (Button) findViewById(R.id.request_btn);
        mRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    // use json str for payload better managed & more exensible for the other to create notification
                    String subject = mSubjectEt.getText().toString();
                    String date = mDateButton.getText().toString();
                    String start = mStartTimeButton.getText().toString();
                    String end = mEndTimeButton.getText().toString();
                    String location = mLocationEt.getText().toString();
                    String description = mDescEt.getText().toString();

                    NotificationPayload payload = new NotificationPayload(subject, date, start, end, location, description,
                            ParseUser.getCurrentUser().getUsername(),
                            ParseUser.getCurrentUser().getString(Profile.FULL_NAME_KEY));
                    String title = "Invitation from " + ParseUser.getCurrentUser().getString(Profile.FULL_NAME_KEY) + "!";
                    CloudCodeUtils.sendNotification(title, payload.toJsonStr(), getIntent().getStringExtra(EXTRA_OHTHER_USERNAME), CloudCodeUtils.APPOINTMENT_REQUEST_MSG_TYPE);
                    finish();
                }
            }
        });
    }

    private boolean validate() {
        if (mSubjectEt.getText().toString().isEmpty()) {
            mSubjectEt.setError("Subject should not be empty.");
            return false;
        }
        // TODO: validate the select date and time is in program range

        // check start time < end time
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date startDate = sdf.parse(mStartTimeButton.getText().toString());
            Date endDate = sdf.parse(mEndTimeButton.getText().toString());
            if (startDate.compareTo(endDate) >= 0) {
                Toast.makeText(this, "Start date should not be earlier thant end date.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }

        return true;
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            int type = getArguments().getInt(TIME_FRAG_EXTRA_TYPE);
            String timeText = hourOfDay + ":" + minute;
            if (type == START_TIME_PICKER) {
                ((Button) getActivity().findViewById(R.id.choose_start_time_btn)).setText(timeText);
            } else if (type == END_TIME_PICKER) {
                ((Button) getActivity().findViewById(R.id.choose_end_time_btn)).setText(timeText);
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = new GregorianCalendar(year, month, day);
            String dateText = sdf.format(calendar.getTime());
            ((Button) getActivity().findViewById(R.id.choose_date_btn)).setText(dateText);
        }
    }

    public static class NotificationPayload {
        final static String SUBJECT_KEY = "subject";
        final static String DATE_KEY = "date";
        final static String STAR_TIME_KEY = "start";
        final static String END_TIME_KEY = "end";
        final static String LOCATION_KEY = "loc";
        final static String DESC_KEY = "desc";
        final static String SENDER_USERNAME_KEY = "send-user";
        final static String SENDER_REAL_NAME_KEY = "send-real";

        String mSubject;
        String mDate;
        String mStartTime;
        String mEndTime;
        String mLocation;
        String mDescription;
        String mSenderUsername;
        String mSenderRealName;

        public static NotificationPayload fromJsonStr(String jsonStr) {
            try {
                JSONObject json = new JSONObject(jsonStr);
                return new NotificationPayload(json.getString(SUBJECT_KEY),
                                               json.getString(DATE_KEY),
                                               json.getString(STAR_TIME_KEY),
                                               json.getString(END_TIME_KEY),
                                               json.getString(LOCATION_KEY),
                                               json.getString(DESC_KEY),
                                               json.getString(SENDER_USERNAME_KEY),
                                               json.getString(SENDER_REAL_NAME_KEY));
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        public String toJsonStr() {
            try {
                JSONObject json = new JSONObject();
                json.put(SUBJECT_KEY, mSubject);
                json.put(DATE_KEY, mDate);
                json.put(STAR_TIME_KEY, mStartTime);
                json.put(END_TIME_KEY, mEndTime);
                json.put(LOCATION_KEY, mLocation);
                json.put(DESC_KEY, mDescription);
                json.put(SENDER_USERNAME_KEY, mSenderUsername);
                json.put(SENDER_REAL_NAME_KEY, mSenderRealName);
                return json.toString();
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        public NotificationPayload(String subject, String date, String startTime, String endTime, String location, String description, String senderUsername, String senderRealName) {
            mSubject = subject;
            mDate = date;
            mStartTime = startTime;
            mEndTime = endTime;
            mLocation = location;
            mDescription = description;
            mSenderUsername = senderUsername;
            mSenderRealName = senderRealName;
        }

        public static String getSubjectKey() {
            return SUBJECT_KEY;
        }

        public static String getDateKey() {
            return DATE_KEY;
        }

        public static String getStarTimeKey() {
            return STAR_TIME_KEY;
        }

        public static String getEndTimeKey() {
            return END_TIME_KEY;
        }

        public static String getLocationKey() {
            return LOCATION_KEY;
        }

        public static String getDescKey() {
            return DESC_KEY;
        }

        public static String getSenderUsernameKey() {
            return SENDER_USERNAME_KEY;
        }

        public static String getSenderRealNameKey() {
            return SENDER_REAL_NAME_KEY;
        }


        public String getSubject() {
            return mSubject;
        }

        public void setSubject(String subject) {
            mSubject = subject;
        }

        public String getDate() {
            return mDate;
        }

        public void setDate(String date) {
            mDate = date;
        }

        public String getStartTime() {
            return mStartTime;
        }

        public void setStartTime(String startTime) {
            mStartTime = startTime;
        }

        public String getEndTime() {
            return mEndTime;
        }

        public void setEndTime(String endTime) {
            mEndTime = endTime;
        }

        public String getLocation() {
            return mLocation;
        }

        public void setLocation(String location) {
            mLocation = location;
        }

        public String getDescription() {
            return mDescription;
        }

        public void setDescription(String description) {
            mDescription = description;
        }

        public String getSenderUsername() {
            return mSenderUsername;
        }

        public void setSenderUsername(String senderUsername) {
            mSenderUsername = senderUsername;
        }

        public String getSenderRealName() {
            return mSenderRealName;
        }

        public void setSenderRealName(String senderRealName) {
            mSenderRealName = senderRealName;
        }

        public String getShortDesc() {
            return mSubject;
        }

        @Override
        public String toString() {
            return String.format("Time: %s-%s, %s\nLocation: %s\nDescription: %s", mStartTime, mEndTime, mDate, mLocation, mDescription);
        }
    }

}
