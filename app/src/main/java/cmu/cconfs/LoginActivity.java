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

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
import cmu.cconfs.model.parseModel.Todo;
import cmu.cconfs.utils.PreferencesManager;

public class LoginActivity extends AppCompatActivity {
    protected PreferencesManager mPreferencesManager;
    public final static int REQUEST_SIGNUP = 1;

    private final static String TAG = LoginActivity.class.getSimpleName();
    private EditText mUsernameText;
    private EditText mPasswordText;
    private Button  mLoginButton;
    private TextView mSignUpLink;
    private TextView mResetLink;

    private Handler mLoginHandler;
    private ProgressDialog mProgressDialog;
    private final static int HANDLE_LOGIN_SUC = 1;
    private final static int HANDLE_LOGIN_FAIL_RETRIEVE_CACHED = 2;
    private final static int HANDLE_LOGIN_FAIL_LOGIN = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login3);

        mUsernameText = (EditText) findViewById(R.id.input_username);
        mPasswordText = (EditText) findViewById(R.id.input_password);
        mLoginButton = (Button) findViewById(R.id.btn_login);

        mSignUpLink = (TextView) findViewById(R.id.link_signup);
        mResetLink = (TextView) findViewById(R.id.link_reset);

        mSignUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
        mResetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        mPreferencesManager = new PreferencesManager(this);

        mLoginHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.arg1) {
                    case HANDLE_LOGIN_SUC:
                        mPreferencesManager.writeBooleanPreference("LoggedIn", true);
                        setResult(RESULT_OK);
                        break;
                    case HANDLE_LOGIN_FAIL_RETRIEVE_CACHED:
                        IMHXSDKHelper.getInstance().logout(true, null);
                        ParseUser.logOut();
                        mPreferencesManager.writeBooleanPreference("LoggedIn", false);
                        Toast.makeText(getApplicationContext(), R.string.login_failure_failed, Toast.LENGTH_LONG).show();
                        break;
                    case HANDLE_LOGIN_FAIL_LOGIN:
                        mPreferencesManager.writeBooleanPreference("LoggedIn", false);
                        String message = msg.getData().getString("msg");
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message, Toast.LENGTH_SHORT).show();
                        break;
                }
                mProgressDialog.dismiss();
                if (getIntent().hasExtra("from")) {
                    Intent intent = new Intent(LoginActivity.this, IMMainActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        };

        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Authenticating...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);


        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUsernameText.getText().toString();
                final String password = mPasswordText.getText().toString();

                if (!validate()) {
                    mLoginButton.setEnabled(true);
                    return;
                }

                // Login
                mProgressDialog.show();
                new Thread(new LoginTask(username, password, mLoginHandler)).start();
                }
        });
    }

    private boolean validate() {
        boolean valid = true;

        String name = mUsernameText.getText().toString();
        String password = mPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 5) {
            mUsernameText.setError("user name at least 5 characters");
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


    public class LoginTask implements Runnable {
        String username, password;
        Handler handler;

        public LoginTask(String username, String password, Handler handler) {
            this.username = username;
            this.password = password;
            this.handler = handler;
        }

        @Override
        public void run() {
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    final Message msg = handler.obtainMessage();

                    if (user != null) {
                        Log.i(TAG, "Start login...");
                        mPreferencesManager.writeBooleanPreference("LoggedIn", true);

                        // 调用sdk登陆方法登陆聊天服务器
                        EMChatManager.getInstance().login(username, password, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                // 登陆成功，保存用户名密码
                                CConfsApplication.getInstance().setUserName(username);
                                CConfsApplication.getInstance().setPassword(password);

                                try {
                                    // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                                    // ** manually load all local groups and
                                    EMGroupManager.getInstance().loadAllGroups();
                                    EMChatManager.getInstance().loadAllConversations();
                                    // 处理好友和群组
                                    initializeContacts();
                                    // send suc message back to looper
                                    msg.arg1 = HANDLE_LOGIN_SUC;
                                    handler.sendMessage(msg);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    // 取好友或者群聊失败，不让进入主页面
                                    // send fail message back to looper
                                    msg.arg1 = HANDLE_LOGIN_FAIL_RETRIEVE_CACHED;
                                    handler.sendMessage(msg);
                                    return;
                                }
                                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(CConfsApplication.currentUserNick.trim());
                                if (!updatenick) {
                                    Log.e("LoginActivity", "update current user nick fail");
                                }
                            }

                            @Override
                            public void onProgress(int progress, String status) {
                            }

                            @Override
                            public void onError(final int code, final String message) {
                                msg.arg1 = HANDLE_LOGIN_FAIL_LOGIN;
                                Bundle data = new Bundle();
                                data.putString("msg", message);
                                msg.setData(data);
                                handler.sendMessage(msg);
                            }
                        });
                        //   finish();
                        // Hooray! The user is logged in.
                    } else {
                        msg.arg1 = HANDLE_LOGIN_FAIL_LOGIN;
                        Bundle data = new Bundle();
                        data.putString("msg", e.getMessage());
                        msg.setData(data);
                        handler.sendMessage(msg);
                    }
                }
            });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                String username = data.getStringExtra(SignUpActivity.EXTRA_USERNAME);
                String password = data.getStringExtra(SignUpActivity.EXTRA_PASSWORD);
//                Log.d(TAG, "Login from registration: " + username + ", " + password);
                mProgressDialog.show();
                new Thread(new LoginTask(username, password, mLoginHandler)).start();
            } else {
                Log.e(TAG, "Sign up not successful: " + requestCode);
            }
        }
    }

}