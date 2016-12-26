package cmu.cconfs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {
    public final static String EXTRA_USERNAME = "username";
    public final static String EXTRA_PASSWORD = "password";
    private final static String TAG = SignUpActivity.class.getSimpleName();

    private EditText mNameText;
    private EditText mEmailText;
    private EditText mUsernameText;
    private EditText mPasswordText;
    private Button mSignupButton;
    private TextView mLinkLogin;

    private Handler mHandler;
    private ProgressDialog mProgressDialog;
    private final static int SIGNUP_SUC = 1;
    private final static int SIGNUP_FAIL = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mNameText = (EditText) findViewById(R.id.input_name);
        mEmailText = (EditText) findViewById(R.id.input_email);
        mUsernameText = (EditText) findViewById(R.id.input_account);
        mPasswordText = (EditText) findViewById(R.id.input_password);

        mSignupButton = (Button) findViewById(R.id.btn_signup);
        mLinkLogin = (TextView) findViewById(R.id.link_login);

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    onSignupFailed();
                    return;
                }
                signUp();
            }
        });

        mLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setMessage(getString(R.string.Is_the_registered));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Registering...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.arg1) {
                    case SIGNUP_SUC:
                        // send the registered user info to login activity for direct login
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
                        Intent data = new Intent();
                        data.putExtra(EXTRA_USERNAME, mUsernameText.getText().toString());
                        data.putExtra(EXTRA_PASSWORD, mPasswordText.getText().toString());
                        setResult(RESULT_OK, data);
                        mProgressDialog.dismiss();
                        finish();
                        break;
                    case SIGNUP_FAIL:
                        String errorMsg = msg.getData().getString("msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                        break;
                }
            }
        };
    }


    private void signUp() {
        mProgressDialog.show();
        ParseUser user = fillUserInfo();
        String username = mUsernameText.getText().toString();
        String password = mPasswordText.getText().toString();
        new Thread(new SignUpTask(user, username, password, mHandler)).start();
    }

    private ParseUser fillUserInfo() {
        final ParseUser user = new ParseUser();
        user.put("full_name", mNameText.getText().toString());
        user.setUsername(mUsernameText.getText().toString());
        user.setEmail(mEmailText.getText().toString());
        user.setPassword(mPasswordText.getText().toString());

        return user;
    }


    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        mSignupButton.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;

        String name = mNameText.getText().toString();
        String email = mEmailText.getText().toString();
        String account = mUsernameText.getText().toString();
        String password = mPasswordText.getText().toString();


        if (name.isEmpty() || name.length() < 3) {
            mNameText.setError("at least 3 characters");
            valid = false;
        } else {
            mNameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailText.setError("enter a valid email address");
            valid = false;
        } else {
            mEmailText.setError(null);
        }

        if (account.isEmpty() || account.length() < 6) {
            mUsernameText.setError("at least 6 characters");
            valid = false;
        } else {
            mUsernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mPasswordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            mPasswordText.setError(null);
        }

        return valid;
    }

    private class SignUpTask implements Runnable {
        ParseUser user;
        String username, password;
        Handler handler;

        public SignUpTask(ParseUser user, String username, String password, Handler handler) {
            this.user = user;
            this.handler = handler;
            this.username = username;
            this.password = password;
        }

        @Override
        public void run() {
            final Message message = handler.obtainMessage();

            try {
                // 调用sdk注册方法
                EMChatManager.getInstance().createAccountOnServer(username,password);
                Log.d(TAG, "em sign up success");

                // 保存用户名
                CConfsApplication.getInstance().setUserName(username);
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d(TAG, "parse sign up success");
                            CConfsApplication.getInstance().setUserName(username);
                            // send suc message
                            message.arg1 = SIGNUP_SUC;
                            mHandler.sendMessage(message);
                            // Hooray! Let them use the app now.
                        } else {
                            handleParseError(message, handler, e);
                        }
                    }
                });
            } catch (final EaseMobException e) {
                handleEMError(message, handler, e);
            }
        }

        private void handleEMError(Message message, Handler handler, EaseMobException e) {
            int errorCode = e.getErrorCode();
            message.arg1 = SIGNUP_FAIL;
            if (errorCode == EMError.NONETWORK_ERROR) {
                message.getData().putString("msg", getResources().getString(R.string.network_anomalies));
            } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                message.getData().putString("msg", getResources().getString(R.string.User_already_exists));
            } else if (errorCode == EMError.UNAUTHORIZED) {
                message.getData().putString("msg", getResources().getString(R.string.registration_failed_without_permission));
            } else if (errorCode == EMError.ILLEGAL_USER_NAME) {
                message.getData().putString("msg", getResources().getString(R.string.illegal_user_name));
            } else {
                message.getData().putString("msg", getResources().getString(R.string.Registration_failed) + e.getMessage());
            }
            handler.sendMessage(message);
        }

        private void handleParseError(Message message, Handler handler, ParseException e) {
            message.arg1 = SIGNUP_FAIL;
            if (e.getMessage().contains("This email has already been registered")) {
                message.getData().putString("msg", "This email has already been registered. You can reset your password at the login page.");
            } else {
                message.getData().putString("msg", e.getMessage());
            }
            handler.sendMessage(message);
        }
    }

}
