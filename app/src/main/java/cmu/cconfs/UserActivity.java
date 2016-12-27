package cmu.cconfs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.parse.ParseUser;

import cmu.cconfs.instantMessage.IMHXSDKHelper;
import cmu.cconfs.instantMessage.activities.*;
import cmu.cconfs.utils.PreferencesManager;

public class UserActivity extends Activity {
    PreferencesManager mPreferencesManager;


    protected Button mSignOutButton;
    protected TextView mCurrentUserTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mCurrentUserTextView = (TextView) findViewById(R.id.current_username);
        mCurrentUserTextView.setText(ParseUser.getCurrentUser().getUsername());
        mPreferencesManager = new PreferencesManager(this);



        mSignOutButton = (Button) findViewById(R.id.signOutButton);
        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void signOut() {
        ParseUser.logOut();
        mPreferencesManager.writeBooleanPreference("LoggedIn", false);
        logout();
    }

    void logout() {
        final ProgressDialog pd = new ProgressDialog(this);
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        IMHXSDKHelper.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        // 重新显示登陆页面
                        finish();
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
