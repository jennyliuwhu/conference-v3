package cmu.cconfs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.HashMap;

import cmu.cconfs.parseUtils.helper.CloudCodeUtils;
import cmu.cconfs.service.FCMRegistrationService;
import cmu.cconfs.utils.PreferencesManager;

public class NotificationDetailActivity extends AppCompatActivity {
    private final static String TAG = NotificationActivity.class.getSimpleName();
    public final static String EXTRA_NOTI_DATA = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_result);

        if (getIntent().hasExtra(EXTRA_NOTI_DATA)) {
            HashMap<String, String> map = (HashMap<String, String>) getIntent().getSerializableExtra(EXTRA_NOTI_DATA);
            Log.d(TAG, "Get intent extra: " + map);
        }
    }

    public void sendNotification(View view) {
        CloudCodeUtils.sendNotification("new title", "new body", ParseUser.getCurrentUser().getUsername(), CloudCodeUtils.APPOINTMENT_REQUEST_MSG_TYPE);
    }

    public void registerToken(View view) {
        CloudCodeUtils.registerToken();
    }

    public void deregisterToken(View view) {
        CloudCodeUtils.deregisterToken();
    }
}
