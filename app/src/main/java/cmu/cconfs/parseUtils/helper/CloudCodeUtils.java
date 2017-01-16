package cmu.cconfs.parseUtils.helper;

import android.util.Log;
import android.view.View;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.HashMap;

import cmu.cconfs.CConfsApplication;
import cmu.cconfs.service.FCMRegistrationService;
import cmu.cconfs.utils.PreferencesManager;

/**
 * Created by qiuzhexin on 1/13/17.
 */

public class CloudCodeUtils {
    private final static String TAG = CloudCodeUtils.class.getSimpleName();
    public final static String APPOINTMENT_REQUEST_MSG_TYPE = "app-requst";
    public final static String APPOINTMENT_ACCEPT_MSG_TYPE = "app-acc";
    public final static String APPOINTMENT_REJECT_MSG_TYPE = "app-reject";
    public final static String APPOINTMENT_CANCEL_MSG_TYPE = "app-cancel";
    public final static String NORMAL_MESSAGE_MSG_TYPE = "message";


    public static void sendNotification(String title, String body, String target, String type) {
        HashMap<String, String> params = new HashMap<>();
        params.put("title", title);
        params.put("body", body);
        params.put("target", target);
        params.put("type", type);

        ParseCloud.callFunctionInBackground("sendMessage", params, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException e) {
                if (e == null) {
                    Log.d(TAG, result);
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    public static void registerToken() {
        HashMap<String, String> params = new HashMap<>();
        PreferencesManager prefManger = new PreferencesManager(CConfsApplication.getInstance(), true);

        params.put("token", prefManger.getStringPreference(FCMRegistrationService.FCM_TOKEN, ""));
        params.put("username", ParseUser.getCurrentUser().getUsername());

        ParseCloud.callFunctionInBackground("registerToken", params, new FunctionCallback<String>() {
            @Override
            public void done(String objId, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "get object id: " + objId);
                    // save the token objectId
                    PreferencesManager preferencesManager = new PreferencesManager(CConfsApplication.getInstance(), true);
                    preferencesManager.writeStringPreference(FCMRegistrationService.FCM_TOKEN_OBJ_ID, objId);
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    public static void deregisterToken() {
        HashMap<String, String> params = new HashMap<>();
        PreferencesManager prefManger = new PreferencesManager(CConfsApplication.getInstance(), true);

        params.put("tokenId", prefManger.getStringPreference(FCMRegistrationService.FCM_TOKEN_OBJ_ID, ""));

        ParseCloud.callFunctionInBackground("deregisterToken", params, new FunctionCallback<String>() {
            @Override
            public void done(String result, ParseException e) {
                if (e == null) {
                    Log.d(TAG, result);
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }
}
