package cmu.cconfs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmu.cconfs.instantMessage.Constant;
import cmu.cconfs.instantMessage.IMHXSDKHelper;
import cmu.cconfs.instantMessage.activities.IMMainActivity;
import cmu.cconfs.instantMessage.db.UserDao;
import cmu.cconfs.instantMessage.domain.User;
import cmu.cconfs.instantMessage.imlib.controller.HXSDKHelper;
import cmu.cconfs.utils.PreferencesManager;

public class LoginActivity extends AppCompatActivity {
    protected Button mLoginButton;
    protected TextView mSignUpTextView;
    protected TextView mResetPasswordTextView;
    protected PreferencesManager mPreferencesManager;

    EditText mUsername;
    EditText mPassword;

    private boolean progressShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login3);


        mSignUpTextView = (TextView) findViewById(R.id.signUpText);
        mResetPasswordTextView = (TextView) findViewById(R.id.resetPasswordText);
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        mResetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
        mPreferencesManager = new PreferencesManager(this);

        mLoginButton = (Button) findViewById(R.id.loginButton);
        mUsername = (EditText) findViewById(R.id.usernameField);
        mPassword = (EditText) findViewById(R.id.passwordField);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                username = username.trim();
                password = password.trim();
                if (username.isEmpty() || password.isEmpty()) {
                    postError("Invalid username/password");

                } else {
                    // Login
                    progressShow = true;
                    final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                    pd.setCanceledOnTouchOutside(false);
                    pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            progressShow = false;
                        }
                    });
                    pd.setMessage(getString(R.string.Is_landing));
                    pd.show();

                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                Log.e("login", "success");
                                Toast.makeText(getApplicationContext(),
                                        "You have been successfully logged in!",
                                        Toast.LENGTH_LONG).show();
                                mPreferencesManager.writeBooleanPreference("LoggedIn", true);

                                final long start = System.currentTimeMillis();
                                // 调用sdk登陆方法登陆聊天服务器
                                EMChatManager.getInstance().login(mUsername.getText().toString(), mPassword.getText().toString(), new EMCallBack() {

                                    @Override
                                    public void onSuccess() {
                                        if (!progressShow) {
                                            return;
                                        }
                                        // 登陆成功，保存用户名密码
                                        CConfsApplication.getInstance().setUserName(mUsername.getText().toString());
                                        CConfsApplication.getInstance().setPassword(mPassword.getText().toString());

                                        try {
                                            // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                                            // ** manually load all local groups and
                                            EMGroupManager.getInstance().loadAllGroups();
                                            EMChatManager.getInstance().loadAllConversations();
                                            // 处理好友和群组
                                            initializeContacts();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            // 取好友或者群聊失败，不让进入主页面
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    pd.dismiss();
                                                    IMHXSDKHelper.getInstance().logout(true, null);
                                                    ParseUser.logOut();
                                                    mPreferencesManager.writeBooleanPreference("LoggedIn", false);
                                                    Toast.makeText(getApplicationContext(), R.string.login_failure_failed, Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            return;
                                        }
                                        // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                                        boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                                                CConfsApplication.currentUserNick.trim());
                                        if (!updatenick) {
                                            Log.e("LoginActivity", "update current user nick fail");
                                        }
                                        if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
                                            pd.dismiss();
                                        }
                                        // 进入主页面
                                        if(getIntent().hasExtra("from")) {
                                            Intent intent = new Intent(LoginActivity.this,
                                                    IMMainActivity.class);
                                            startActivity(intent);
                                        }
                                        finish();
                                    }

                                    @Override
                                    public void onProgress(int progress, String status) {
                                    }

                                    @Override
                                    public void onError(final int code, final String message) {
                                        if (!progressShow) {
                                            return;
                                        }
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                pd.dismiss();
                                                Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });


                             //   finish();
                                // Hooray! The user is logged in.
                            } else {
                                postError(e.getMessage());
                            }
                        }
                    });

                }
            }
        });



    }

    private void postError(String error){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(error)
                .setTitle("Please try again")
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initializeContacts() {
        Map<String, User> userlist = new HashMap<String, User>();
        // 添加user"申请与通知"
        User newFriends = new User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(
                R.string.Application_and_notify);
        newFriends.setNick(strChat);

        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
        User groupUser = new User();
        String strGroup = getResources().getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userlist.put(Constant.GROUP_USERNAME, groupUser);

        // 添加"Robot"
        User robotUser = new User();
        String strRobot = getResources().getString(R.string.robot_chat);
        robotUser.setUsername(Constant.CHAT_ROBOT);
        robotUser.setNick(strRobot);
        robotUser.setHeader("");
        userlist.put(Constant.CHAT_ROBOT, robotUser);

        // 存入内存
        ((IMHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);
        // 存入db
        UserDao dao = new UserDao(LoginActivity.this);
        List<User> users = new ArrayList<User>(userlist.values());
        dao.saveContactList(users);
    }


}
