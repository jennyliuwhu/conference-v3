package cmu.cconfs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import cmu.cconfs.adapter.TodoListAdapter;
import cmu.cconfs.model.parseModel.Todo;
import cmu.cconfs.model.parseModel.TodoCached;
import cmu.cconfs.parseUtils.helper.LoadingUtils;
import cmu.cconfs.utils.AccountUtils;
import cmu.cconfs.utils.PreferencesManager;

public class TodoListActivity extends AppCompatActivity {
    private final static String TAG = TodoListActivity.class.getSimpleName();

    private final static int REQUEST_LOGIN = 1;
    private final static int REQUEST_EDIT_TODO = 2;

    private Toolbar mToolbar;

    private TextView mLoginInfo;

    private ParseQueryAdapter<Todo> mTodoListAdapter;
    private ListView mTodoListView;
    private LinearLayout mNoTodoListView;

    private PreferencesManager mPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        mPreferencesManager = new PreferencesManager(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mLoginInfo = (TextView) findViewById(R.id.loggedin_info);

        mTodoListView = (ListView) findViewById(R.id.todo_list_view);
        mNoTodoListView = (LinearLayout) findViewById(R.id.no_todos_view);
        mTodoListView.setEmptyView(mNoTodoListView);

        ParseQueryAdapter.QueryFactory<Todo> factory = new ParseQueryAdapter.QueryFactory<Todo>() {
            @Override
            public ParseQuery<Todo> create() {
                ParseQuery<Todo> query = Todo.getQuery();
                query.orderByDescending("createdAt");
                query.fromLocalDatastore();
                return query;
            }
        };

        mTodoListAdapter = new TodoListAdapter(this, factory);
        mTodoListView.setAdapter(mTodoListAdapter);

        mTodoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Todo todo = mTodoListAdapter.getItem(i);
                openEditView(todo);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLoggedIn()) {
            syncTodosToParse();
            updateLoggedInInfo();
            updateUserCachedTodo(ParseUser.getCurrentUser());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todo_list_memu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                if (ParseUser.getCurrentUser() != null) {
                    Intent i = new Intent(this, NewTodoActivity.class);
                    startActivityForResult(i, REQUEST_EDIT_TODO);
                }
                break;
            case R.id.action_sync:
                syncTodosToParse();
                break;
            case R.id.action_logout:
                final ProgressDialog pd = new ProgressDialog(TodoListActivity.this);
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
                        updateLoggedInInfo();
                        mTodoListAdapter.clear();
                        pd.dismiss();
                    }
                }.execute();
                break;
            case R.id.action_login:
                Intent i = new Intent(this, LoginActivity.class);
                startActivityForResult(i, REQUEST_LOGIN);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        boolean realUser = isLoggedIn();
        menu.findItem(R.id.action_login).setVisible(!realUser);
        menu.findItem(R.id.action_logout).setVisible(realUser);
        return true;
    }

    private void openEditView(Todo todo) {
        Intent i = new Intent(this, NewTodoActivity.class);
        i.putExtra("ID", todo.getUuidString());
        startActivityForResult(i, REQUEST_EDIT_TODO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_EDIT_TODO:
                if (resultCode != RESULT_OK) {
                    Log.e(TAG, "Error editing todo item: " + resultCode);
                    return;
                }
                mTodoListAdapter.loadObjects();
                break;
            case REQUEST_LOGIN:
                if (resultCode != RESULT_OK) {
                    Log.e(TAG, "Error Login/Sign Up: " + resultCode);
                    return;
                }
                if (ParseUser.getCurrentUser().isNew()) {
                    syncTodosToParse();
                } else {
                    loadFromParse();
                }
                break;
        }
    }

    private void updateLoggedInInfo() {
        if (isLoggedIn()) {
            ParseUser user = ParseUser.getCurrentUser();
            mLoginInfo.setText(getString(R.string.logged_in, user.getString("full_name")));
        } else {
            mLoginInfo.setText(getString(R.string.not_logged_in));
        }
    }

    private void syncTodosToParse() {
        if (!LoadingUtils.isNetworkAvailable()) {
            Toast.makeText(this, "Not network available, some todos may not be synced with cloud.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!isLoggedIn()) {
            // login before saving the todos
            Intent i = new Intent(this, LoginActivity.class);
            startActivityForResult(i, REQUEST_LOGIN);
            return;
        }

        // saving draft todos to cloud
        ParseQuery<Todo> query = Todo.getQuery();
        query.fromPin(Todo.PIN_TAG);
        query.whereEqualTo("isDraft", true);
        query.findInBackground(new FindCallback<Todo>() {
            @Override
            public void done(List<Todo> objects, ParseException e) {
                if (e == null) {
                    for (final Todo todo : objects) {
                        todo.setDraft(false);
                        todo.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    if (!isFinishing()) {
                                        mTodoListAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    todo.setDraft(true);
                                }
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "Error finding local to do list items: " + e.getMessage());
                }
            }
        });
    }

    private  void loadFromParse() {
        ParseQuery<Todo> query = Todo.getQuery();
        final ParseUser user =  ParseUser.getCurrentUser();
        query.whereEqualTo("author", user);
        query.findInBackground(new FindCallback<Todo>() {
            @Override
            public void done(List<Todo> todos, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground(Todo.PIN_TAG, todos, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                markUserTodoLocalCached(user);
                                if (!isFinishing()) {
                                    mTodoListAdapter.loadObjects();
                                }
                            } else {
                                Log.e(TAG, "Error pinning todos: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "Error retrieving todos from Parse: " + e.getMessage());
                }
            }
        });
    }

    private boolean isLoggedIn() {
        return mPreferencesManager.getBooleanPreference("LoggedIn", false);
    }

    private void updateUserCachedTodo(final ParseUser user) {
        ParseQuery<TodoCached> query = TodoCached.getQuery();
        query.whereEqualTo("author", user);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<TodoCached>() {
            @Override
            public void done(List<TodoCached> cached, ParseException e) {
                if (e == null) {
                    if (cached.isEmpty()) {
                        loadFromParse();
                        markUserTodoLocalCached(user);
                    }
                } else {
                    Log.e(TAG, "Error trying to find whether todo cached for user: " + e.getMessage());
                }
            }
        });
    }

    private void markUserTodoLocalCached(ParseUser user) {
        TodoCached c = new TodoCached();
        c.setAuthor(user);
        c.pinInBackground();
    }

}
