package cmu.cconfs.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cmu.cconfs.service.SyncCalendarService;

/**
 * Created by qiuzhexin on 1/3/17.
 */

public class SyncCalendarAlarmReceiver extends BroadcastReceiver {
    private final static String TAG = SyncCalendarAlarmReceiver.class.getSimpleName();
    public final static int REQUEST_CODE = 12345;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received alarm: " + context);
        Intent i = new Intent(context, SyncCalendarService.class);
        context.startService(i);
    }
}
