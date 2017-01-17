package cmu.cconfs.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import cmu.cconfs.NotificationDetailActivity;
import cmu.cconfs.R;
import cmu.cconfs.model.parseModel.Profile;
import cmu.cconfs.parseUtils.helper.CloudCodeUtils;

/**
 * Created by qiuzhexin on 1/16/17.
 */

public class SendMessageFragment extends DialogFragment {
    private static final String TAG = SendMessageFragment.class.getSimpleName();
    public static final String OTHER_USERNAME_KEY = "other-username";

    public SendMessageFragment() {

    }

    public static SendMessageFragment newInstance(String username) {
        SendMessageFragment fragment = new SendMessageFragment();
        Bundle bundle = new Bundle();
        bundle.putString(OTHER_USERNAME_KEY, username);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_send_message, null);
        final EditText titleEt = (EditText) contentView.findViewById(R.id.title_et);
        final EditText detailEt = (EditText) contentView.findViewById(R.id.detail_et);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String otherUsername = getArguments().getString(OTHER_USERNAME_KEY);

        builder.setTitle("Message");
        builder.setView(contentView);
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // send the message
                String title = titleEt.getText().toString();
                String detail = detailEt.getText().toString();
                NotificationPayload payload = new NotificationPayload(detail, ParseUser.getCurrentUser().getUsername(), title, ParseUser.getCurrentUser().getString(Profile.FULL_NAME_KEY));
                CloudCodeUtils.sendNotification(title, payload.toJsonStr(), otherUsername, CloudCodeUtils.NORMAL_MESSAGE_MSG_TYPE);

                // if is a reply message finsh the message detail
                if (getActivity().getClass().getSimpleName().equals(NotificationDetailActivity.class.getSimpleName())) {
                    getActivity().finish();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.setCancelable(false);
        return builder.create();
    }

    public static class NotificationPayload {
        public static String MESSAGE_KEY = "message";
        public static String SENDER_USERNAME_KEY = "sender-username";
        public static String TITLE_KEY = "title";
        public static String SENDER_REAL_NAME_KEY = "sender-real-name";

        String mMessage;
        String mSenderUsername;
        String mTitle;
        String mSenderRealName;

        public NotificationPayload(String message, String username, String title, String realName) {
            mMessage = message;
            mSenderUsername = username;
            mTitle = title;
            mSenderRealName = realName;
        }

        public static NotificationPayload fromJsonStr(String jsonStr) {
            try {
                JSONObject json = new JSONObject(jsonStr);
                String msg = json.getString(MESSAGE_KEY);
                String sender = json.getString(SENDER_USERNAME_KEY);
                String title = json.getString(TITLE_KEY);
                String senderRealName = json.getString(SENDER_REAL_NAME_KEY);
                return new NotificationPayload(msg, sender, title, senderRealName);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        public String toJsonStr() {
            try {
                JSONObject json = new JSONObject();
                json.put(MESSAGE_KEY, mMessage);
                json.put(SENDER_USERNAME_KEY, mSenderUsername);
                json.put(TITLE_KEY, mTitle);
                json.put(SENDER_REAL_NAME_KEY, mSenderRealName);
                return json.toString();
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        public String getMessage() {
            return mMessage;
        }

        public void setMessage(String message) {
            mMessage = message;
        }

        public String getSenderUsername() {
            return mSenderUsername;
        }

        public void setSenderUsername(String senderUsername) {
            mSenderUsername = senderUsername;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public String getSenderRealName() {
            return mSenderRealName;
        }

        public void setSenderRealName(String senderRealName) {
            mSenderRealName = senderRealName;
        }
    }
}
