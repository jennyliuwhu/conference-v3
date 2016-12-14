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
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmu.cconfs.adapter.PaperListViewAdapter;
import cmu.cconfs.model.parseModel.Paper;

public class SessionActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    private View mHeaderView;
    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mBaseTranslationY;
    private String notesSharedPref;
    private String imageSharedPref;
    private String sessionKey;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;

    private List<Paper> papers = null;
    private Map<String, String> imageNameToImageFilePathMap;

    private Uri fileUri;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        imageNameToImageFilePathMap = new HashMap<>();
        image1 = (ImageView) findViewById(R.id.session_image1);
        image2 = (ImageView) findViewById(R.id.session_image2);
        image3 = (ImageView) findViewById(R.id.session_image3);

        registerListenersForImageViews();

        String pNames = getIntent().getStringExtra("papers");
        //populate papers
        if (pNames != null && !pNames.isEmpty() && !pNames.trim().equals("0")) {
            String[] paperNames = pNames.split(",");
            ParseQuery<Paper> query = Paper.getQuery();
            query.fromLocalDatastore();
            query.whereContainedIn("unique_id", new ArrayList<String>(Arrays.asList(paperNames)));
            query.fromPin(Paper.PIN_TAG);
            try {
                papers = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String sessionTime = getIntent().getStringExtra("sessionTime");
        String sessionName = getIntent().getStringExtra("sessionName");
        String sessionRoom = getIntent().getStringExtra("sessionRoom");
        String sessionChair = getIntent().getStringExtra("sessionChair");

        sessionKey = sessionName + sessionChair + sessionRoom + sessionTime;
        notesSharedPref = sessionKey + "note";
        imageSharedPref = sessionKey + "image";

        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        String imageUris = settings.getString(imageSharedPref, "");
        if (!imageUris.isEmpty()) {
            populateImages(imageUris);
        }

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // session title, time and room
        getSupportActionBar().setTitle(sessionName);
        TextView stickyTextView = (TextView) findViewById(R.id.sticky);
        stickyTextView.setText(sessionTime + ", Room " + sessionRoom);
        // session chairman
        if (sessionChair == null || sessionChair.length() == 0) {
            CardView cardView = (CardView) findViewById(R.id.container);
            cardView.setVisibility(View.GONE);
        } else {
            TextView textView_chairman = (TextView) findViewById(R.id.chairman);
            textView_chairman.setText(sessionChair);
        }
        // session paper
        ListView listView_paper = (ListView) findViewById(R.id.listView_paper);
        PaperListViewAdapter adapter = new PaperListViewAdapter(this, papers);
        listView_paper.setAdapter(adapter);

        listView_paper.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Paper paper = (Paper)adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getBaseContext(), PaperActivity.class);
                intent.putExtra("paperTitle", paper.getTitle());
                intent.putExtra("paperAbstract", paper.getAbstract());
                intent.putExtra("paperAuthor", paper.getAuthorWithAffiliation());
                intent.putExtra("sessionKey",sessionKey);
                startActivity(intent);
            }
        });

        mHeaderView = findViewById(R.id.header);
        ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
        mToolbarView = findViewById(R.id.toolbar);

        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

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

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (dragging) {
            int toolbarHeight = mToolbarView.getHeight();
            if (firstScroll) {
                float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
                if (-toolbarHeight < currentHeaderTranslationY) {
                    mBaseTranslationY = scrollY;
                }
            }
            float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -toolbarHeight, 0);
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        mBaseTranslationY = 0;

        if (scrollState == ScrollState.DOWN) {
            showToolbar();
        } else if (scrollState == ScrollState.UP) {
            int toolbarHeight = mToolbarView.getHeight();
            int scrollY = mScrollView.getCurrentScrollY();
            if (toolbarHeight <= scrollY) {
                hideToolbar();
            } else {
                showToolbar();
            }
        } else {
            // Even if onScrollChanged occurs without scrollY changing, toolbar should be adjusted
            if (!toolbarIsShown() && !toolbarIsHidden()) {
                // Toolbar is moving but doesn't know which to move:
                // you can change this to hideToolbar()
                showToolbar();
            }
        }
    }

    private void registerListenersForImageViews() {
        image1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SessionActivity.this, ImageDetailsActivity.class);
                intent.putExtra("image", imageNameToImageFilePathMap.get("image1"));
                intent.putExtra("sessionKey", imageSharedPref);
                startActivity(intent);
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SessionActivity.this, ImageDetailsActivity.class);
                intent.putExtra("image", imageNameToImageFilePathMap.get("image2"));
                intent.putExtra("sessionKey", imageSharedPref);
                startActivity(intent);
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SessionActivity.this, ImageDetailsActivity.class);
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

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mHeaderView) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mHeaderView) == -mToolbarView.getHeight();
    }

    private void showToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
        }
    }

    private void hideToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        int toolbarHeight = mToolbarView.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(-toolbarHeight).setDuration(200).start();
        }
    }
}
