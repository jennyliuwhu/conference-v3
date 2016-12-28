package cmu.cconfs;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import cmu.cconfs.model.parseModel.Profile;

public class ProfileActivity extends AppCompatActivity {
    private final static String TAG = ProfileActivity.class.getSimpleName();

    private Button mSaveChangeButton;
    private Button mLogoutButton;

    private FloatingActionMenu mFloatingActionMenu;
    private FloatingActionButton mSettingButton;
    private FloatingActionButton mSendNotesButton;

    private ImageButton mProfileImage;
    private AppBarLayout mAppBarLayout;
    private RelativeLayout mPhoneBox;
    private RelativeLayout mEmailBox;
    private RelativeLayout mCompanyBox;
    private RelativeLayout mTitleBox;
    private RelativeLayout mDescBox;
    private TextView mPhoneTv;
    private TextView mEmailTv;
    private TextView mCompanyTv;
    private TextView mTitleTv;
    private TextView mDescTv;

    private Profile mProfile;

    private AlertDialog mEditTextAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // profile related changes
        mProfileImage = (ImageButton) findViewById(R.id.user_profile_photo);
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Profile image clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mAppBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppBarLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.main_background));
            }
        });

        mPhoneBox = (RelativeLayout) findViewById(R.id.phone_box);
        mEmailBox = (RelativeLayout) findViewById(R.id.email_box);
        mCompanyBox = (RelativeLayout) findViewById(R.id.company_box);
        mTitleBox = (RelativeLayout) findViewById(R.id.title_box);
        mDescBox = (RelativeLayout) findViewById(R.id.desc_box);

        mPhoneTv = (TextView) findViewById(R.id.phone_number_tv);
        mEmailTv = (TextView) findViewById(R.id.email_tv);
        mCompanyTv = (TextView) findViewById(R.id.company_tv);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mDescTv = (TextView) findViewById(R.id.description_tv);

        mPhoneBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = getEditTextAlertDialog("Phone");
                dialog.show();
                final EditText editText = (EditText) dialog.findViewById(R.id.edit_text);
                editText.setText(mPhoneTv.getText().toString());
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = editText.getText().toString();
                        if (Patterns.PHONE.matcher(text).matches()) {
                            mPhoneTv.setText(text);
                            dialog.dismiss();
                        } else {
                            editText.setError("Enter a valid number");
                        }
                    }
                });
            }
        });

        mEmailBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = getEditTextAlertDialog("Email");
                dialog.show();
                final EditText editText = (EditText) dialog.findViewById(R.id.edit_text);
                editText.setText(mEmailTv.getText().toString());
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = editText.getText().toString();
                        if (Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                            mEmailTv.setText(text);
                            dialog.dismiss();
                        } else {
                            editText.setError("Enter a valid email");
                        }
                    }
                });
            }
        });

        mCompanyBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = getEditTextAlertDialog("Company");
                dialog.show();
                final EditText editText = (EditText) dialog.findViewById(R.id.edit_text);
                editText.setText(mCompanyTv.getText().toString());
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = editText.getText().toString();
                        mCompanyTv.setText(text);
                        dialog.dismiss();
                    }
                });
            }
        });

        mTitleBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = getEditTextAlertDialog("Title");
                dialog.show();
                final EditText editText = (EditText) dialog.findViewById(R.id.edit_text);
                editText.setText(mTitleTv.getText().toString());
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = editText.getText().toString();
                        mTitleTv.setText(text);
                        dialog.dismiss();
                    }
                });
            }
        });

        mDescBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = getEditTextAlertDialog("Description");
                dialog.show();
                final EditText editText = (EditText) dialog.findViewById(R.id.edit_text);
                editText.setText(mDescTv.getText().toString());
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = editText.getText().toString();
                        mDescTv.setText(text);
                        dialog.dismiss();
                    }
                });
            }
        });

        mSaveChangeButton = (Button) findViewById(R.id.btn_save_change);

        // account setting related
        mLogoutButton = (Button) findViewById(R.id.btn_sign_out);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Log out button clicked", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });


        mFloatingActionMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        mSettingButton = (FloatingActionButton) mFloatingActionMenu.findViewById(R.id.fab_setting);
        mSendNotesButton = (FloatingActionButton) mFloatingActionMenu.findViewById(R.id.fab_email_notes);

        mSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackNotify(view, "setting clicked");
            }
        });

        mSendNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackNotify(view, "send notes clicked");
            }
        });


        // fetch profile and load the screen
        new FetchProfileTask().execute();
    }

    private void snackNotify(View v, String s) {
        Snackbar.make(v, s, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    private AlertDialog getEditTextAlertDialog(String title) {
        if (mEditTextAlertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this, R.style.AppTheme_Dark_Dialog);
            builder.setTitle(title);
            LayoutInflater inflater = LayoutInflater.from(ProfileActivity.this);
            View editView = inflater.inflate(R.layout.edit_text_wraper, null);
            final EditText editText = (EditText) editView.findViewById(R.id.edit_text);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (!mSaveChangeButton.isEnabled()) {
                        Log.d(TAG, "Edit text changes");
                        mSaveChangeButton.setEnabled(true);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            builder.setView(editView);
            builder.setPositiveButton("CONFIRM", null);
            builder.setNegativeButton("CANCEL", null);
            builder.setCancelable(false);

            mEditTextAlertDialog = builder.create();
        }

        mEditTextAlertDialog.setTitle(title);
        return mEditTextAlertDialog;
    }

    private class FetchProfileTask extends AsyncTask<Void, Void, Profile> {

        @Override
        protected Profile doInBackground(Void... voids) {
            ParseQuery<Profile> query = Profile.getQuery();
            query.whereEqualTo(Profile.PARSE_USER_KEY, ParseUser.getCurrentUser());

            Profile profile = null;
            try {
                profile = query.getFirst();
            } catch (ParseException e) {
                Log.e(TAG, "Error fetching profile for user: " + e.getMessage());
            }

            return profile;
        }

        @Override
        protected void onPostExecute(Profile profile) {
            if (profile == null) {
                Log.d(TAG, "no profile associated with the user");
                return;
            }

            mProfile = profile;

            mPhoneTv.setText(profile.getPhone());
            mEmailTv.setText(profile.getEmail());
            mCompanyTv.setText(profile.getCompany());
            mTitleTv.setText(profile.getTitle());
            mDescTv.setText(profile.getDescription());
            getSupportActionBar().setTitle(profile.getFullName());

            profile.getProfileImage().getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        mProfileImage.setImageBitmap(bmp);
                    } else {
                        Log.e(TAG, "Error loading profile image: " + e.getMessage());
                    }
                }
            });

            profile.getBackgroundImage().getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        mAppBarLayout.setBackground(new BitmapDrawable(getApplicationContext().getResources(), bmp));
                    }
                }
            });
        }
    }
}

