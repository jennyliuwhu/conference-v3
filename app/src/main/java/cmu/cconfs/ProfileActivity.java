package cmu.cconfs;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.parse.DeleteCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import cmu.cconfs.model.parseModel.Profile;
import cmu.cconfs.parseUtils.helper.LoadingUtils;
import cmu.cconfs.utils.AccountUtils;
import cmu.cconfs.utils.image.BitmapScaler;

public class ProfileActivity extends AppCompatActivity {
    private final static String TAG = ProfileActivity.class.getSimpleName();

    private Button mSaveChangeButton;
    private Button mLogoutButton;

    private FloatingActionMenu mFloatingActionMenu;
    private FloatingActionButton mSettingButton;
    private FloatingActionButton mSendNotesButton;

    private ImageView mProfileImageView;
    private AppBarLayout mAppBarLayout;
    private RelativeLayout mFullnameBox;
    private RelativeLayout mPhoneBox;
    private RelativeLayout mEmailBox;
    private RelativeLayout mCompanyBox;
    private RelativeLayout mTitleBox;
    private RelativeLayout mDescBox;
    private TextView mFullnameTv;
    private TextView mPhoneTv;
    private TextView mEmailTv;
    private TextView mCompanyTv;
    private TextView mTitleTv;
    private TextView mDescTv;

    private ParseFile mProfileImg;
    private ParseFile mBackgroundImg;
    private String mProfileImgFilename;
    private String mBackgroundImgFilename;
    private final static int REQUEST_GET_PROFILE_IMG_TAKE_PHOTO = 1;
    private final static int REQUEST_GET_BACKGROUND_IMG_TAKE_PHOTO = 2;
    private final static int REQUEST_GET_PROFILE_IMG_GALLERY = 3;
    private final static int REQUEST_GET_BACKGROUND_IMG_GALLERY = 4;


    private AlertDialog mEditTextAlertDialog;
    private static String[] mImageSourceOptions = { "Take a photo", "Choose from gallery" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // create unique filenames for profile image & background image
        mProfileImgFilename = ParseUser.getCurrentUser().getUsername() + "_profile.jpg";
        mBackgroundImgFilename = ParseUser.getCurrentUser().getUsername() + "_background.jpg";

        // profile related changes
        mProfileImageView = (ImageView) findViewById(R.id.user_profile_photo);
        mProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackNotify(view, "profile img clicked");
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this, R.style.AppTheme_Dark_Dialog);
                builder.setTitle("Change profile image");

                ListView listView = new ListView(view.getContext());
                listView.setAdapter(new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, mImageSourceOptions));
                builder.setView(listView);
                final AlertDialog dialog = builder.show();

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == 0) {
                            Log.d(TAG, "Take a photo");
                            onOpenCamera(REQUEST_GET_PROFILE_IMG_TAKE_PHOTO, mProfileImgFilename);
                        } else {
                            Log.d(TAG, "Select from gallery");
                            onOpenGallery(REQUEST_GET_PROFILE_IMG_GALLERY);
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mAppBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackNotify(view, "background img clicked");
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this, R.style.AppTheme_Dark_Dialog);
                builder.setTitle("Change background image");

                ListView listView = new ListView(view.getContext());
                listView.setAdapter(new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, mImageSourceOptions));
                builder.setView(listView);
                final AlertDialog dialog = builder.show();

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == 0) {
                            Log.d(TAG, "Take a photo");
                            onOpenCamera(REQUEST_GET_BACKGROUND_IMG_TAKE_PHOTO, mBackgroundImgFilename);
                        } else {
                            Log.d(TAG, "Select from gallery");
                            onOpenGallery(REQUEST_GET_BACKGROUND_IMG_GALLERY);
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        mFullnameBox = (RelativeLayout) findViewById(R.id.fullname_box);
        mPhoneBox = (RelativeLayout) findViewById(R.id.phone_box);
        mEmailBox = (RelativeLayout) findViewById(R.id.email_box);
        mCompanyBox = (RelativeLayout) findViewById(R.id.company_box);
        mTitleBox = (RelativeLayout) findViewById(R.id.title_box);
        mDescBox = (RelativeLayout) findViewById(R.id.desc_box);

        mFullnameTv = (TextView) findViewById(R.id.fullname_tv);
        mPhoneTv = (TextView) findViewById(R.id.phone_number_tv);
        mEmailTv = (TextView) findViewById(R.id.email_tv);
        mCompanyTv = (TextView) findViewById(R.id.company_tv);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mDescTv = (TextView) findViewById(R.id.description_tv);

        mFullnameBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = getEditTextAlertDialog("Name");
                dialog.show();
                final EditText editText = (EditText) dialog.findViewById(R.id.edit_text);
                editText.setError(null);
                editText.setText(mFullnameTv.getText().toString());
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = editText.getText().toString();
                        if (!text.isEmpty() && text.length() > 3) {
                            mFullnameTv.setText(text);
                            dialog.dismiss();
                        } else {
                            editText.setError("at least 3 characters");
                        }
                    }
                });
            }
        });

        mPhoneBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = getEditTextAlertDialog("Phone");
                dialog.show();
                final EditText editText = (EditText) dialog.findViewById(R.id.edit_text);
                editText.setError(null);
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
                editText.setError(null);
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
                editText.setError(null);
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
                editText.setError(null);
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
                editText.setError(null);
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
        mSaveChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this, R.style.MyDialogTheme);
                builder.setTitle("Save Profle");
                builder.setMessage("You sure want to save the profile changes?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new UpdateUserProfileTask().execute();
                        mSaveChangeButton.setEnabled(false);
                    }
                });
                builder.setNegativeButton("CANCEL", null);
                builder.show();
            }
        });

        // account setting related
        mLogoutButton = (Button) findViewById(R.id.btn_sign_out);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Log out button clicked", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                final ProgressDialog pd = new ProgressDialog(ProfileActivity.this);
                String st = getResources().getString(R.string.Are_logged_out);
                pd.setMessage(st);
                pd.setCanceledOnTouchOutside(false);
                pd.show();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        AccountUtils.logoutUser(getApplicationContext());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        pd.dismiss();
                        finish();
                    }
                }.execute();
            }
        });


        mFloatingActionMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        mSettingButton = (FloatingActionButton) mFloatingActionMenu.findViewById(R.id.fab_setting);
        mSendNotesButton = (FloatingActionButton) mFloatingActionMenu.findViewById(R.id.fab_email_notes);

        mSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackNotify(view, "setting clicked");
                Intent i = new Intent(getApplicationContext(), PreferenceActivity.class);
                startActivity(i);
            }
        });

        mSendNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackNotify(view, "send notes clicked");
                Intent i = new Intent(getApplicationContext(), SendNotesActivity.class);
                startActivity(i);
            }
        });


        // fetch profile and load the screen
        new FetchProfileTask().execute();

        getSupportActionBar().setTitle(null);
    }

    private void onOpenCamera(int requestCode, String fileName) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(fileName));

        if (i.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(i, requestCode);
        }
    }

    private void onOpenGallery(int requestCode) {
        Intent i = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (i.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(i, requestCode);
        }
    }

    private Uri getPhotoFileUri(String fileName) {
        if (isExternalStorageAvailable()) {
            File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CC App tag");

            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdir()) {
                Log.e(TAG, "Failed to create directory for image file");
            }

            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }

        Log.e(TAG, "Could not create Uri for file name: " + fileName);
        return null;
    }

    private boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
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

    private Profile getUserProfile() {
        ParseQuery<Profile> query = Profile.getQuery();
        query.whereEqualTo(Profile.PARSE_USER_KEY, ParseUser.getCurrentUser());

        // check network connection
        if (!LoadingUtils.isNetworkAvailable()) {
            query.fromLocalDatastore();
        }

        Profile profile = null;
        try {
            profile = query.getFirst();
        } catch (ParseException e) {
            Log.e(TAG, "Error fetching profile for user: " + e.getMessage());
        }

        return profile;
    }

    private void updateUserProfile() throws ParseException {
        Profile profile = getUserProfile();
        if (profile != null) {
            profile.setFullName(mFullnameTv.getText().toString());
            profile.setPhone(mPhoneTv.getText().toString());
            profile.setEmail(mEmailTv.getText().toString());
            profile.setCompany(mCompanyTv.getText().toString());
            profile.setTitle(mTitleTv.getText().toString());
            profile.setDescription(mDescTv.getText().toString());

            if (mBackgroundImg != null) {
                mBackgroundImg.save();
                profile.setBackgroundImage(mBackgroundImg);
            }
            if (mProfileImg != null) {
                mProfileImg.save();
                profile.setProfileImage(mProfileImg);
            }

            profile.saveEventually();
        }
    }

    private class FetchProfileTask extends AsyncTask<Void, Void, Profile> {

        @Override
        protected Profile doInBackground(Void... voids) {
            return getUserProfile();
        }

        @Override
        protected void onPostExecute(final Profile profile) {
            if (profile == null) {
                Log.d(TAG, "No profile associated with the user");
                return;
            }

            // update the local profile
            profile.unpinInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        return;
                    }
                    profile.pinInBackground();
                }
            });

            mFullnameTv.setText(profile.getFullName());
            mPhoneTv.setText(profile.getPhone());
            mEmailTv.setText(profile.getEmail());
            mCompanyTv.setText(profile.getCompany());
            mTitleTv.setText(profile.getTitle());
            mDescTv.setText(profile.getDescription());

            if (profile.getProfileImage() != null) {
                profile.getProfileImage().getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                            mProfileImageView.setImageDrawable(getProfileImageDrawable(bmp));
                        } else {
                            Log.e(TAG, "Error loading profile image: " + e.getMessage());
                        }
                    }
                });
            }

            if (profile.getBackgroundImage() != null) {
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

    private class UpdateUserProfileTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                updateUserProfile();
            } catch (ParseException e) {
                Log.e(TAG, "Error updating user profile: " + e.getMessage());
            }
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mSaveChangeButton.isEnabled()) {
            mSaveChangeButton.setEnabled(true);
        }

        switch (requestCode) {
            case REQUEST_GET_PROFILE_IMG_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Bitmap bmp = BitmapScaler.rotateBitmapOrientation(getPhotoFileUri(mProfileImgFilename).getPath());
                    Bitmap resized = BitmapScaler.scaleToFill(bmp, 120, 120);
                    mProfileImageView.setImageDrawable(getProfileImageDrawable(resized));
                    mProfileImg = new ParseFile(mProfileImgFilename, convertBitmapToByteArray(resized));
                } else {
                    Log.e(TAG, "Error retrieve profile image");
                }
                break;
            case REQUEST_GET_BACKGROUND_IMG_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Bitmap bmp = BitmapScaler.rotateBitmapOrientation(getPhotoFileUri(mBackgroundImgFilename).getPath());
                    Bitmap resized = BitmapScaler.scaleToFitHeight(bmp, 218);
                    mAppBarLayout.setBackground(new BitmapDrawable(getApplicationContext().getResources(), resized));
                    mBackgroundImg = new ParseFile(mBackgroundImgFilename, convertBitmapToByteArray(resized));
                } else {
                    Log.e(TAG, "Error retrieve background image");
                }
                break;
            case REQUEST_GET_PROFILE_IMG_GALLERY:
                if (resultCode == RESULT_OK && data != null) {
                    try {
                        Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        Bitmap resized = BitmapScaler.scaleToFill(bmp, 120, 120);
                        mProfileImageView.setImageDrawable(getProfileImageDrawable(resized));
                        mProfileImg = new ParseFile(mProfileImgFilename, convertBitmapToByteArray(resized));
                    } catch (IOException e) {
                        Log.e(TAG, "Error get bitmap from picker: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Error retrieve profile image from gallery");
                }
                break;
            case REQUEST_GET_BACKGROUND_IMG_GALLERY:
                if (resultCode == RESULT_OK && data != null) {
                    try {
                        Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        Bitmap resized = BitmapScaler.scaleToFitHeight(bmp, 218);
                        mAppBarLayout.setBackground(new BitmapDrawable(getApplicationContext().getResources(), resized));
                        mBackgroundImg = new ParseFile(mBackgroundImgFilename, convertBitmapToByteArray(resized));
                    } catch (IOException e) {
                        Log.e(TAG, "Error get bitmap from picker: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Error retrieve background image from gallery");
                }
                break;
        }
    }

    private Bitmap convertFilenameToBitmap(String filename) {
        Uri takenPhotoUri = getPhotoFileUri(filename);
        Log.d(TAG, "photo path: " + takenPhotoUri.getPath());
        Bitmap bmp = BitmapFactory.decodeFile(takenPhotoUri.getPath());

        return bmp;
    }

    private byte[] convertBitmapToByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing byte stream.");
        }
        return bytes;
    }

    private Drawable getProfileImageDrawable(Bitmap bitmap) {
        return BitmapScaler.scaleToCircle(bitmap, 60);
    }

}

