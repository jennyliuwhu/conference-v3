package cmu.cconfs.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.RemoteMessage.Notification;

import java.util.HashMap;
import java.util.Map;

import cmu.cconfs.NotificationDetailActivity;
import cmu.cconfs.R;

/**
 * Created by qiuzhexin on 1/13/17.
 */

public class FCMMessageHandlerService extends FirebaseMessagingService {
    private final static String TAG = FCMRegistrationService.class.getSimpleName();
    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String from = remoteMessage.getFrom();
        Log.d(TAG, "Sent from: " + from);
        Log.d(TAG, "Received data: " + data);


//        Notification notification = remoteMessage.getNotification();
        createNotification(convertToSerializableMap(data));
    }

    // Creates notification based on title and body received
    private void createNotification(HashMap<String, String> data) {
        // prepare intent for notification detail
        Intent i = new Intent(this, NotificationDetailActivity.class);
        i.putExtra(NotificationDetailActivity.EXTRA_NOTI_DATA, data);
        int requestId = (int) System.currentTimeMillis();
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
        PendingIntent pIntent = PendingIntent.getActivity(this, requestId, i, flags);

        // create notification
        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_perm_device_information_white_24dp)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(data.get("title"))
                .setContentText(data.get("body"))
                .setVibrate(new long[] { 500, 500, 500 })
                .setContentIntent(pIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }

    private HashMap<String, String> convertToSerializableMap(Map<String, String> map) {
        HashMap<String, String> serializableMap = new HashMap<>();

        for (String key : map.keySet()) {
            serializableMap.put(key, map.get(key));
        }

        return serializableMap;
    }

}
