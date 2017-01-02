package cmu.cconfs;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmu.cconfs.model.parseModel.Note;
import cmu.cconfs.model.parseModel.Rate;
import cmu.cconfs.utils.PreferencesManager;

public class PaperActivity extends AppCompatActivity {

    private String notesSharedPref;
    private String imageSharedPref;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private RatingBar publicRatingBar;

    private Map<String, String> imageNameToImageFilePathMap;

    private Uri fileUri;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

    // useful note reference
    private String mPaperInfo;
    private String mSessionKey;
    public static String PAPER_INFO_SEPARATOR = "|";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_paper);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setTitle("Paper");

        imageNameToImageFilePathMap = new HashMap<>();
        image1 = (ImageView) findViewById(R.id.paper_image1);
        image2 = (ImageView) findViewById(R.id.paper_image2);
        image3 = (ImageView) findViewById(R.id.paper_image3);

        registerListenersForImageViews();

        String paperTitle = getIntent().getStringExtra("paperTitle");
        String paperAbstract = getIntent().getStringExtra("paperAbstract");
        String paperAuthor = getIntent().getStringExtra("paperAuthor");
        String sessionKey = getIntent().getStringExtra("sessionKey");


        mSessionKey = sessionKey;
        mPaperInfo = getPaperInfo( new String[] {paperTitle, paperAuthor} );
        final String paperKey = sessionKey + paperTitle;
        notesSharedPref = paperKey + "note";
        imageSharedPref = paperKey + "image";

        final SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        String imageUris = settings.getString(imageSharedPref, "");
        if (!imageUris.isEmpty()) {
            populateImages(imageUris);
        }

        TextView textView_paperTitle = (TextView) findViewById(R.id.paperTitle);
        TextView textView_paperAbstract = (TextView) findViewById(R.id.abstractContent);
        TextView textView_paperAuthor = (TextView) findViewById(R.id.authorContent);

        textView_paperTitle.setText(paperTitle);
        textView_paperAbstract.setText(paperAbstract);
        textView_paperAuthor.setText(paperAuthor);

        final RatingBar localRatingBar = (RatingBar) findViewById(R.id.paper_local_ratingBar);
        publicRatingBar = (RatingBar) findViewById(R.id.paper_public_ratingBar);
        setPublicRatingBar(paperKey);
        final String ratingSharedPref = paperKey + "rating";
        float rating = settings.getFloat(ratingSharedPref, 0f);
        localRatingBar.setRating(rating);

        localRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                PreferencesManager mPreferencesManager = new PreferencesManager(getBaseContext());

                boolean loggedIn = mPreferencesManager.getBooleanPreference("LoggedIn", false);

                if (!loggedIn) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                } else {
                    Rate rate = new Rate();
                    rate.setPaperKey(paperKey);
                    rate.setRate((double) v);
                    rate.setRater(ParseUser.getCurrentUser().getUsername());
                    rate.saveInBackground();
                    setPublicRatingBar(paperKey);
                    final SharedPreferences.Editor editor = settings.edit();
                    editor.putFloat(ratingSharedPref, v);
                    editor.commit();
                }
            }
        });

    }


    public void didTapCameraButton(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new ContentValues());
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void didTapVideoButton(View view) {
        // create Intent to take a video and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        fileUri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new ContentValues());
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, fileUri);
        // start the video capture Intent
        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
    }

    public void didTapNoteButton(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText editText = new EditText(getApplicationContext());

        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();

        String previousText = settings.getString(notesSharedPref, "");
        editText.setText(previousText);
        editText.setTextColor(Color.BLACK);

        alert.setMessage("Enter notes");
        alert.setTitle("Notes");
        alert.setView(editText);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String editTextValue = editText.getText().toString();
                editor.putString(notesSharedPref, editTextValue);
                editor.commit();
                Log.e("edit", editTextValue);
                // save the paper notes in local datastore
                savePaperNote(editTextValue);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }

    public void didTapShowAllImages(View view) {
        Intent intent = new Intent(this, ImageGridViewActivity.class);
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        String imagesPaths = settings.getString(imageSharedPref, "");
        intent.putExtra("imagesPaths", imagesPaths);
        intent.putExtra("sessionKey", imageSharedPref);
        startActivity(intent);
    }

    public void didTapShowAllPublicImages(View view) {
        Intent intent = new Intent(this, ImageGridViewActivity.class);
        intent.putExtra("sessionKey", imageSharedPref);
        intent.putExtra("isPublic", true);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Photo is saved", Toast.LENGTH_LONG).show();

                String imageUri = getRealPathFromURI(fileUri);
                if (!hasImage(image3)) {
                    populateImages(imageUri);
                }

                SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                String imageUris = settings.getString(imageSharedPref, "");
                if (!imageUris.isEmpty()) {
                    imageUris += "," + imageUri;
                } else {
                    imageUris = imageUri;
                }
                editor.putString(imageSharedPref, imageUris);
                editor.commit();

            }
        }

        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video is saved", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }
    }

    private void setPublicRatingBar(String paperKey) {
        ParseQuery<Rate> query = ParseQuery.getQuery(Rate.class);
        query.whereEqualTo("paperKey", paperKey);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<Rate>() {
            @Override
            public void done(List<Rate> objects, ParseException e) {
                if (objects != null && objects.size() > 0) {
                    double avg = 0;
                    double sum = 0;
                    for (Rate object : objects) {
                        sum += object.getRate();
                    }
                    avg = sum / objects.size();
                    publicRatingBar.setRating((float) avg);
                }
            }
        });
    }

    private void registerListenersForImageViews() {
        image1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ImageDetailsActivity.class);
                intent.putExtra("image", imageNameToImageFilePathMap.get("image1"));
                intent.putExtra("sessionKey", imageSharedPref);
                startActivity(intent);
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ImageDetailsActivity.class);
                intent.putExtra("image", imageNameToImageFilePathMap.get("image2"));
                intent.putExtra("sessionKey", imageSharedPref);
                startActivity(intent);
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ImageDetailsActivity.class);
                intent.putExtra("image", imageNameToImageFilePathMap.get("image3"));
                intent.putExtra("sessionKey", imageSharedPref);
                startActivity(intent);
            }
        });
    }

    private void populateImages(String imageUris) {
        if (imageUris == null || imageUris.isEmpty()) {
            return;
        }

        String[] str = imageUris.split(",");
        for (String s : str) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            // downsizing image as it throws OutOfMemory Exception for larger images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(s, options);
            if (!hasImage(image1)) {
                image1.setImageBitmap(bitmap);
                image1.setVisibility(View.VISIBLE);
                imageNameToImageFilePathMap.put("image1", s);
            } else if (!hasImage(image2)) {
                image2.setImageBitmap(bitmap);
                image2.setVisibility(View.VISIBLE);
                imageNameToImageFilePathMap.put("image2", s);
            } else if (!hasImage(image3)) {
                image3.setImageBitmap(bitmap);
                image3.setVisibility(View.VISIBLE);
                imageNameToImageFilePathMap.put("image3", s);
            } else {
                break;
            }
        }

    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return hasImage;
    }

    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    private String getPaperInfo(String[] data) {
        StringBuffer sb = new StringBuffer();
        for (String d : data) {
            sb.append(d).append(PAPER_INFO_SEPARATOR);
        }
        return sb.toString();
    }

    private void savePaperNote(final String content) {
        ParseQuery<Note> query = Note.getQuery();
        query.fromPin(Note.PAPER_PIN_TAG);
        query.fromLocalDatastore();
        query.whereEqualTo("session_info", mSessionKey);
        query.whereEqualTo("paper_info", mPaperInfo);
        query.whereEqualTo("author", ParseUser.getCurrentUser());
        query.getFirstInBackground(new GetCallback<Note>() {
            @Override
            public void done(Note note, ParseException e) {
                if (e != null || note == null) {
                    note = new Note();
                    note.setAuthor(ParseUser.getCurrentUser());
                    note.setSessionInfo(mSessionKey);
                    note.setPaperInfo(mPaperInfo);
                }
                note.setContent(content);
                note.pinInBackground(Note.PAPER_PIN_TAG);
            }
        });
    }

}
