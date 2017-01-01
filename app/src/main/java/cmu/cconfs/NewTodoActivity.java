package cmu.cconfs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import cmu.cconfs.model.parseModel.Todo;

public class NewTodoActivity extends AppCompatActivity {
    private final static String TAG = NewTodoActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private EditText mTodoText;
    private Button mSaveButton;
    private Button mDeleteButton;
    private Todo mTodo;
    private String mTodoID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_todo);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (getIntent().hasExtra("ID")) {
            mTodoID = getIntent().getStringExtra("ID");
            Log.i(TAG, "Get todo uuid: " + mTodoID);
        }

        mTodoText = (EditText) findViewById(R.id.todo_text);
        mSaveButton = (Button) findViewById(R.id.saveButton);
        mDeleteButton = (Button) findViewById(R.id.deleteButton);

        if (mTodoID == null) {
            mTodo = new Todo();
            mTodo.setUuidString();
        } else {
            ParseQuery<Todo> query = Todo.getQuery();
            query.fromLocalDatastore();
            query.whereEqualTo("uuid", mTodoID);
            query.getFirstInBackground(new GetCallback<Todo>() {

                @Override
                public void done(Todo object, ParseException e) {
                    if (!isFinishing()) {
                        mTodo = object;
                        mTodoText.setText(mTodo.getTitle());
                        mDeleteButton.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTodo.setTitle(mTodoText.getText().toString());
                mTodo.setDraft(true);
                mTodo.setAuthor(ParseUser.getCurrentUser());
                mTodo.setACL(new ParseACL(ParseUser.getCurrentUser())); // restrict the access to only the creator
                mTodo.pinInBackground(Todo.PIN_TAG, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (isFinishing()) {
                            return;
                        }
                        if (e == null) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Error pinning todo: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTodo.deleteEventually();
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
