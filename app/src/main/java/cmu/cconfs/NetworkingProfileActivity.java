package cmu.cconfs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;

import cmu.cconfs.fragment.SendMessageFragment;
import cmu.cconfs.model.parseModel.Profile;
import cmu.cconfs.parseUtils.helper.CloudCodeUtils;
import cmu.cconfs.utils.AccountUtils;
import cmu.cconfs.utils.image.BitmapScaler;

public class NetworkingProfileActivity extends AppCompatActivity {
    private final static String TAG = ProfileActivity.class.getSimpleName();
    public final static String EXTRA_PROFILE_USERNAME = "profile";

    private Button mSaveChangeButton;
    private Button mLogoutButton;

    private FloatingActionMenu mFloatingActionMenu;
    private FloatingActionButton mMakeAppointmentButton;
    private FloatingActionButton mSendMessageButton;

    private ImageView mProfileImageView;
    private AppBarLayout mBackground;
    private TextView mFullnameTv;
    private TextView mPhoneTv;
    private TextView mEmailTv;
    private TextView mCompanyTv;
    private TextView mTitleTv;
    private TextView mDescTv;

    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // load the profile views
        mProfileImageView = (ImageView) findViewById(R.id.user_profile_photo);
        mBackground = (AppBarLayout) findViewById(R.id.app_bar);
        mFullnameTv = (TextView) findViewById(R.id.fullname_tv);
        mPhoneTv = (TextView) findViewById(R.id.phone_number_tv);
        mEmailTv = (TextView) findViewById(R.id.email_tv);
        mCompanyTv = (TextView) findViewById(R.id.company_tv);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mDescTv = (TextView) findViewById(R.id.description_tv);

        // others' profile doesn't provide these options
        mSaveChangeButton = (Button) findViewById(R.id.btn_save_change);
        mSaveChangeButton.setVisibility(View.GONE);
        mLogoutButton = (Button) findViewById(R.id.btn_sign_out);
        mLogoutButton.setVisibility(View.GONE);

        mFloatingActionMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        mMakeAppointmentButton = (FloatingActionButton) mFloatingActionMenu.findViewById(R.id.fab_setting);
        mSendMessageButton = (FloatingActionButton) mFloatingActionMenu.findViewById(R.id.fab_email_notes);
        mMakeAppointmentButton.setImageResource(R.drawable.ic_contacts_white_24dp);
        mMakeAppointmentButton.setLabelText("Make Appointment");
        mSendMessageButton.setImageResource(R.drawable.ic_message_white_24dp);
        mSendMessageButton.setLabelText("Send Message");

        mMakeAppointmentButton.setEnabled(false);
        mMakeAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start appointment detail view passed with target username
                Intent i = new Intent(view.getContext(), AppointmentActivity.class);
                i.putExtra(AppointmentActivity.EXTRA_OHTHER_USERNAME, mUsername);
                i.putExtra(AppointmentActivity.EXTRA_OHTHER_REAL_NAME, mFullnameTv.getText());
                startActivity(i);
            }
        });

        mSendMessageButton.setEnabled(false);
        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start message detail view passed with target username
                SendMessageFragment fragment = SendMessageFragment.newInstance(mUsername);
                fragment.show(getSupportFragmentManager(), "send-msg-frag");
            }
        });

        // restrict visibility to login user
        if (!AccountUtils.isUserLogined(this)) {
            mFloatingActionMenu.setVisibility(View.INVISIBLE);
        }

        mUsername = getIntent().getStringExtra(EXTRA_PROFILE_USERNAME);
        fetchUserProfile();
    }

    private void fetchUserProfile() {
        new AsyncTask<Void, Void, Profile>() {
            @Override
            protected Profile doInBackground(Void... voids) {
                Profile profile = null;
                try {
                    ParseUser user = ParseUser.getQuery().whereEqualTo("username", mUsername).getFirst();
                    profile = Profile.getQuery().whereEqualTo(profile.PARSE_USER_KEY, user).getFirst();
                } catch (ParseException e) {
                    Log.e(TAG, e.getMessage());
                }
                return profile;
            }

            @Override
            protected void onPostExecute(Profile profile) {
                if (profile != null) {
                    if (profile.getProfileImage() != null) {
                        profile.getProfileImage().getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e != null) {
                                    Log.e(TAG, "Error loading profile image: " + e.getMessage());
                                    return;
                                }
                                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                mProfileImageView.setImageDrawable(getProfileImageDrawable(bmp));
                            }
                        });
                    }
                    if (profile.getBackgroundImage() != null) {
                        profile.getBackgroundImage().getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e != null) {
                                    Log.e(TAG, "Error loading background image: " + e.getMessage());
                                    return;
                                }
                                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                mBackground.setBackground(new BitmapDrawable(getApplicationContext().getResources(), bmp));
                            }
                        });
                    }
                    mFullnameTv.setText(profile.getFullName());
                    mPhoneTv.setText(profile.getPhone());
                    mEmailTv.setText(profile.getEmail());
                    mCompanyTv.setText(profile.getCompany());
                    mTitleTv.setText(profile.getTitle());
                    mDescTv.setText(profile.getDescription());

                    mMakeAppointmentButton.setEnabled(true);
                    mSendMessageButton.setEnabled(true);
                }
            }
        }.execute();

    }

    private Drawable getProfileImageDrawable(Bitmap bitmap) {
        return BitmapScaler.scaleToCircle(bitmap, 60);
    }
}
