package cmu.cconfs.service;

import android.app.IntentService;
import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * Created by qiuzhexin on 1/13/17.
 */

public class InstanceIDListenerService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify of changes
        Intent intent = new Intent(this, FCMRegistrationService.class);
        startService(intent);
    }

}
