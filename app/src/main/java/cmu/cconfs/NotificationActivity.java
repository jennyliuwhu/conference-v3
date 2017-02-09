package cmu.cconfs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cmu.cconfs.adapter.NotificationAdapter;
import cmu.cconfs.model.parseModel.Message;
import cmu.cconfs.parseUtils.helper.LoadingUtils;
import cmu.cconfs.utils.PreferencesManager;
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class NotificationActivity extends AppCompatActivity implements WaveSwipeRefreshLayout.OnRefreshListener {
    private final static String TAG = NotificationActivity.class.getSimpleName();
    public static PreferencesManager mPreferencesManager;
    private ListView mListview;
    private Toolbar toolbar;

    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;

    private List<Message> messages;
    private NotificationAdapter adapter;

    // set up drawer
    public final static int REQUEST_SIGN_IN = 1;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setNavigationBarColor(Color.BLACK);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification);
        mPreferencesManager = new PreferencesManager(this);
        initView();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setTitle("Notification");

        // setup drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nvView);
        mDrawerToggle = setupDrawerToggle();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        setupNavigationView(mNavigationView);
    }
    private void setupNavigationView(NavigationView navView) {
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        Intent i = new Intent();
        switch (menuItem.getItemId()) {
            case R.id.nav_dash_board:
                Toast.makeText(getApplicationContext(), "dash board clicked", Toast.LENGTH_SHORT).show();
                i.setClass(this, HomeActivity.class);
                startActivity(i);
                break;
            case R.id.nav_my_schedule:
                Toast.makeText(getApplicationContext(), "my schedule clicked", Toast.LENGTH_SHORT).show();
                i.setClass(getApplicationContext(), ScheduleActivity.class);
                startActivity(i);
                break;
            case R.id.nav_my_profile:
                Toast.makeText(getApplicationContext(), "my profile clicked", Toast.LENGTH_SHORT).show();
                i = getLoginStatusIntent(ProfileActivity.class, LoginActivity.class);
                startActivityForResult(i, REQUEST_SIGN_IN);
                break;
            case R.id.nav_my_to_do_list:
                Toast.makeText(getApplicationContext(), "my todo list clicked", Toast.LENGTH_SHORT).show();
                i.setClass(this, TodoListActivity.class);
                startActivity(i);
                break;
            case R.id.nav_sync_data:
                syncScheduleData(this);
                break;
            case R.id.nav_log_in:
                i = getLoginStatusIntent(UserActivity.class, LoginActivity.class);
                startActivityForResult(i, REQUEST_SIGN_IN);
                break;
        }

        menuItem.setCheckable(true);
        setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private Intent getLoginStatusIntent(Class loginTarget, Class notLoginTarget) {
        boolean loggedIn = mPreferencesManager.getBooleanPreference("LoggedIn",false);
        Toast .makeText(this, loggedIn + "", Toast.LENGTH_SHORT).show();
        Intent i = new Intent();
        if(!loggedIn) {
            i.setClass(getApplicationContext(), notLoginTarget);
        } else {
            i.setClass(getApplicationContext(), loginTarget);
        }
        return i;
    }

    private void syncScheduleData(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("Sync Data");
        builder.setMessage("You sure want to reload the backend data?");
        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final ProgressDialog pd = new ProgressDialog(context);
                String st = "Syncing data...";
                pd.setMessage(st);
                pd.setCanceledOnTouchOutside(false);
                pd.show();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            LoadingUtils.loadFromParse();
                            LoadingUtils.populateDataProvider();
                            LoadingUtils.populateRoomProvider();
                        } catch (ParseException e) {
                            Log.e(TAG, e.getMessage());
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        pd.dismiss();
                    }
                }.execute();
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void initView() {
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE, Color.WHITE);
        mWaveSwipeRefreshLayout.setOnRefreshListener(this);
        mWaveSwipeRefreshLayout.setWaveColor(0xff32cd80);

        //mWaveSwipeRefreshLayout.setMaxDropHeight(1500);

        mListview = (ListView) findViewById(R.id.main_list);
        adapter = new NotificationAdapter(this, messages);
        mListview.setAdapter(adapter);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.action_sending);
        ParseUser currentUser = ParseUser.getCurrentUser();

        Set<String> admins = new HashSet<>(Arrays.asList(getResources().getStringArray(R.array.admin_mail_address)));
        item.setVisible(false);

        if(currentUser != null) {
            Log.i(TAG,"User name: " + currentUser.getUsername() );
            Log.i(TAG,"User email: " + currentUser.getEmail());
            if (admins.contains(currentUser.getEmail())) {
                item.setVisible(true);
                Log.i(TAG, "Set the admin send notification button visible");
            }
        }
        else {
            Log.w(TAG, "No parse user cached");
            // ask the user to sign in
            Intent i = new Intent();
            i.setClass(NotificationActivity.this, LoginActivity.class);
            startActivity(i);
        }
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    private void refresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ParseQuery<Message> query = Message.getQuery();
                query.orderByDescending("createdAt");
                try {
                    messages = query.find();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                adapter.updateDataSet(messages);
                adapter.notifyDataSetChanged();
                mWaveSwipeRefreshLayout.setRefreshing(false);

            }
        }, 3000);
    }

    @Override
    protected void onResume() {

        super.onResume();
        mWaveSwipeRefreshLayout.setRefreshing(true);

        refresh();
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mWaveSwipeRefreshLayout.setRefreshing(true);
            refresh();
            return true;
        } else if(id == R.id.action_sending) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), SendNotificationActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
