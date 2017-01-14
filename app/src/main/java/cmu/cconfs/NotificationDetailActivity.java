package cmu.cconfs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NotificationDetailActivity extends AppCompatActivity {
    public final static String EXTRA_NOTI_DATA = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_result);
    }
}
