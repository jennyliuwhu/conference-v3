package cmu.cconfs.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import cmu.cconfs.R;
import cmu.cconfs.utils.PreferencesManager;

/**
 * Created by qiuzhexin on 1/13/17.
 */

public class FCMRegistrationService extends IntentService {
    private static final String TAG = FCMRegistrationService.class.getSimpleName();

    public static final String FCM_TOKEN = "FCMToken";
    public static final String FCM_TOKEN_OBJ_ID = "ObjectId";

    private PreferencesManager mPreferencesManager;

    public FCMRegistrationService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPreferencesManager = new PreferencesManager(this, true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Make a call to Instance API
        FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance();
        String senderId = getResources().getString(R.string.gcm_defaultSenderId);

        // request token that will be used by the server to send push notifications
        String token = instanceID.getToken();
        Log.d(TAG, "FCM Registration Token: " + token);

        // record the registration token in shared preference
        mPreferencesManager.writeStringPreference(FCM_TOKEN, token);
    }
}
