package cmu.cconfs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
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

    private final static String TAG = SignUpActivity.class.getSimpleName();

    private EditText mNameText;
    private EditText mEmailText;
    private EditText mUsernameText;
    private EditText mPasswordText;
    private Button mSignupButton;
    private TextView mLinkLogin;

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

                signUp(fillUserInfo());
            }
        });

        mLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private ParseUser fillUserInfo() {
        final ParseUser user = new ParseUser();
        user.put("full_name", mNameText.getText().toString());
        user.setUsername(mUsernameText.getText().toString());
        user.setEmail(mEmailText.getText().toString());
        user.setPassword(mPasswordText.getText().toString());

        return user;
    }

    private void postError(String error){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);

        builder.setMessage(error)
                .setTitle("Please try again")
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void signUp(final ParseUser user){
        final ProgressDialog pd = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        pd.setMessage(getResources().getString(R.string.Is_the_registered));
        pd.show();

        new Thread(new Runnable() {
            public void run() {
                try {
                    // 调用sdk注册方法
                    EMChatManager.getInstance().createAccountOnServer(mUsernameText.getText().toString(), mPasswordText.getText().toString());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!SignUpActivity.this.isFinishing())
                                pd.dismiss();
                            // 保存用户名
                            CConfsApplication.getInstance().setUserName(mUsernameText.getText().toString());
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();

                            user.signUpInBackground(new SignUpCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.e("sign up", "success");
                                        Toast.makeText(getApplicationContext(), "Sign up Success!", Toast.LENGTH_LONG).show();
                                        try {
                                            EMChatManager.getInstance().createAccountOnServer(mUsernameText.getText().toString(), mPasswordText.getText().toString());
                                            // 保存用户名
                                            CConfsApplication.getInstance().setUserName(mUsernameText.getText().toString());
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
                                            finish();
                                        } catch (final EaseMobException ee) {
                                            int errorCode = ee.getErrorCode();
                                            if (errorCode == EMError.NONETWORK_ERROR) {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                                            } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                                            } else if (errorCode == EMError.UNAUTHORIZED) {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                                            } else if (errorCode == EMError.ILLEGAL_USER_NAME) {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed) + ee.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        // Hooray! Let them use the app now.
                                    } else {
                                        if (e.getMessage().contains("This email has already been registered")) {
                                            postError("This email has already been registered. You can reset your password at the login page.");
                                        } else
                                            postError(e.getMessage());
                                    }
                                }
                            });

                            finish();
                        }
                    });
                } catch (final EaseMobException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!SignUpActivity.this.isFinishing())
                                pd.dismiss();
                            int errorCode=e.getErrorCode();
                            if(errorCode== EMError.NONETWORK_ERROR){
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.USER_ALREADY_EXISTS){
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.UNAUTHORIZED){
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.ILLEGAL_USER_NAME){
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
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

}
