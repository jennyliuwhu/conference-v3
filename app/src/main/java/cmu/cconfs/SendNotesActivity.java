package cmu.cconfs;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cmu.cconfs.model.parseModel.Note;
import cmu.cconfs.model.parseModel.SessionImage;

public class SendNotesActivity extends AppCompatActivity {
    private final static String TAG = SendNotesActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private Button mSendButton;
    private EditText mEmailEditText;
    private CheckBox mNotesCheckBox;
    private CheckBox mImagesCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notes);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mEmailEditText = (EditText) findViewById(R.id.email_edit_text);

        mSendButton = (Button) findViewById(R.id.send_notes_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmailEditText.getText().toString();
                if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmailEditText.setError("Email format not correct");
                } else { // email is valid, get & send notes
                    String content = mNotesCheckBox.isChecked() ? createNotesJsonContent() : null;
                    List<String> imagePaths = mImagesCheckBox.isChecked() ? getImagePaths() : null;
                    sendArtifacts(content, email, imagePaths);
                }
            }
        });

        mNotesCheckBox = (CheckBox) findViewById(R.id.email_notes_checkbox);
        mImagesCheckBox = (CheckBox) findViewById(R.id.email_images_checkbox);
    }

    private void sendArtifacts(String content, String email, List<String> filePaths) {
        Log.d(TAG, "Sending to " + email + "...\n" + content);
        Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{ email });
        i.putExtra(Intent.EXTRA_SUBJECT, "Artifacts in CConfs app");
        if (content != null) {
            i.putExtra(Intent.EXTRA_TEXT, content);
        }
        if (filePaths != null) {
            ArrayList<Uri> uris = new ArrayList<>();
            for (String path : filePaths) {
                Uri uri = Uri.fromFile(new File(path));
                uris.add(uri);
            }
            i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        }
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SendNotesActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private String createNotesJsonContent() {
        JSONObject jsonDoc = new JSONObject();

        ParseQuery<Note> query = Note.getQuery();
        query.fromLocalDatastore();
        query.fromPin(Note.SESSION_PIN_TAG);
        query.whereEqualTo("author", ParseUser.getCurrentUser());
        try {
            // all session notes
            List<Note> sessionNotes = query.find();
            JSONArray sessionNotesJsonArray = new JSONArray();
            for (Note note : sessionNotes) {
                JSONObject json = new JSONObject();
                json.put("session_info", note.getSessionInfo());
                json.put("content", note.getContent());
                sessionNotesJsonArray.put(json);
            }
            jsonDoc.put("SessionNotes", sessionNotesJsonArray);
            // all paper notes
            query.fromPin(Note.PAPER_PIN_TAG);
            List<Note> paperNotes = query.find();
            JSONArray paperNotesJsonArray = new JSONArray();
            for (Note note : paperNotes) {
                JSONObject json = new JSONObject();
                json.put("session_info", note.getSessionInfo());
                json.put("paper_info", note.getPaperInfo());
                json.put("content", note.getContent());
                paperNotesJsonArray.put(json);
            }
            jsonDoc.put("PaperNotes", paperNotesJsonArray);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return jsonDoc.toString();
    }

    private List<String> getImagePaths() {
        ParseQuery<SessionImage> query = SessionImage.getQuery();
        query.fromLocalDatastore();
        query.whereEqualTo("author", ParseUser.getCurrentUser());
        List<String> paths = new ArrayList<>();
        try {
            List<SessionImage> images = query.find();
            for (SessionImage image : images) {
                for (String p : image.getImagePaths().split(",")) {
                    paths.add(p);
                }
            }
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
        return paths;
    }

}
