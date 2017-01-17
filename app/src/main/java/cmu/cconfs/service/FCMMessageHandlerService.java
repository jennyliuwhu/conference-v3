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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cmu.cconfs.AppointmentActivity;
import cmu.cconfs.NotificationDetailActivity;
import cmu.cconfs.R;
import cmu.cconfs.fragment.SendMessageFragment;
import cmu.cconfs.parseUtils.helper.CloudCodeUtils;

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

        createNotification(convertToSerializableMap(data));
    }

    // Creates notification based on title and body received
    private void createNotification(HashMap<String, String> data) {
        // create notification and configure
        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_perm_device_information_white_24dp)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(data.get("title"))
                .setVibrate(new long[] { 500, 500, 500 })
                .setAutoCancel(true);

        String messageType = data.get("type");
        switch (messageType) {
            case CloudCodeUtils.APPOINTMENT_REQUEST_MSG_TYPE:
                // prepare intent for appointment request notification detail
                Intent i = new Intent(this, NotificationDetailActivity.class);
                i.putExtra(NotificationDetailActivity.EXTRA_NOTI_DATA, data);
                int requestId = (int) System.currentTimeMillis();
                int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
                PendingIntent pIntent = PendingIntent.getActivity(this, requestId, i, flags);
                mBuilder.setContentIntent(pIntent);

                // deserialize the body content and retrieve short description
                AppointmentActivity.NotificationPayload payload = AppointmentActivity.NotificationPayload.fromJsonStr(data.get("body"));
                mBuilder.setContentText(payload.getShortDesc());
                break;
            case CloudCodeUtils.NORMAL_MESSAGE_MSG_TYPE:
                // prepare intent for normal message notification detail
                i = new Intent(this, NotificationDetailActivity.class);
                i.putExtra(NotificationDetailActivity.EXTRA_NOTI_DATA, data);
                requestId = (int) System.currentTimeMillis();
                flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
                pIntent = PendingIntent.getActivity(this, requestId, i, flags);
                mBuilder.setContentIntent(pIntent);

                // deserialize message body content;
                SendMessageFragment.NotificationPayload msgPayload = SendMessageFragment.NotificationPayload.fromJsonStr(data.get("body"));
                mBuilder.setContentText(msgPayload.getMessage());
                break;
            default:
                mBuilder.setContentText(data.get("body"));
                break;
        }

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }

    private HashMap<String, String> convertToSerializableMap(Map<String, String> map) {
        HashMap<String, String> serializableMap = new HashMap<>();

        for (String key : map.keySet()) {
            String value = map.get(key);
            serializableMap.put(key, value);
        }
        Log.d(TAG, "serialized map: " + serializableMap);
        return serializableMap;
    }

}
