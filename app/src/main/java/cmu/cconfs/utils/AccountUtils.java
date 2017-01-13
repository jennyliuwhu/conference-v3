package cmu.cconfs.utils;

/**
 * Created by zmhbh on 6/16/15.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;

import java.util.concurrent.CountDownLatch;

import cmu.cconfs.instantMessage.IMHXSDKHelper;

public class AccountUtils {

    private static final String KEY_ACCOUNT_NAME = "account_name";

    // Cache the currently logged in user in memory. This will cause
    // problems if you directly modify the shared preferences; all
    // modifications to the shared preferences must be made through
    // the AccountUtils utility class!
    private static String mCurrentUser = null;

//    public static Account getGoogleAccountByName(Context ctx, String accountName) {
//        if (accountName != null) {
//            AccountManager am = AccountManager.get(ctx);
//            Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
//            for (Account account : accounts) {
//                if (accountName.equals(account.name)) {
//                    return account;
//                }
//            }
//        }
//        return null;
//    }

    public static String getAccountName(Context ctx) {
        if (mCurrentUser != null) {
            return mCurrentUser;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getString(KEY_ACCOUNT_NAME, null);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void setAccountName(Context ctx, String accountName) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        editor.putString(KEY_ACCOUNT_NAME, accountName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
        mCurrentUser = accountName;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void removeAccount(Context ctx) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        editor.remove(KEY_ACCOUNT_NAME);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
        mCurrentUser = null;
    }

    public static void logoutUser(final Context context) {
        // logout parse user
        ParseUser.logOut();

        // logout em user
        final CountDownLatch latch = new CountDownLatch(1);
        IMHXSDKHelper.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                latch.countDown();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                latch.countDown();
                Toast.makeText(context, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
            }
        });

        // record login status
        PreferencesManager preferencesManager = new PreferencesManager(context);
        preferencesManager.writeBooleanPreference("LoggedIn", false);

        // create a anonymous user
        ParseAnonymousUtils.logIn(null);

        // wait for log out to be finished
        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.e("account util", e.getMessage());
        }
    }

}