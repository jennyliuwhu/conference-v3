package cmu.cconfs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.parse.ParseException;

import org.askerov.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.Arrays;

import cmu.cconfs.adapter.HomeGridDynamicAdapter;
import cmu.cconfs.parseUtils.helper.LoadingUtils;
import cmu.cconfs.utils.PreferencesManager;


public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getName();
    private DynamicGridView gridView;
    private String[] titles = {"Agenda", "My Schedule", "Room Schedule","Map", "Floor Guide", "Sponsor", "Notification", "About", "Setting", "Chat", "Nearby", "Authors", "Network", "Travel", "Transfer"};
    public static PreferencesManager mPreferencesManager;

    public final static int REQUEST_SIGN_IN = 1;

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mPreferencesManager = new PreferencesManager(this);

        gridView = (DynamicGridView) findViewById(R.id.dynamic_grid);
        gridView.setAdapter(new HomeGridDynamicAdapter(this
                , new ArrayList<String>(Arrays.asList(titles))
                , getResources().getInteger(R.integer.column_count)));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast .makeText(HomeActivity.this, parent.getAdapter().getItem(position).toString(),
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                switch (position) {
                    //agenda
                    case 0:
                        intent.setClass(getApplicationContext(), AgendaActivity.class);
                        startActivity(intent);
                        break;

                    //schedule
                    case 1:
                        intent.setClass(getApplicationContext(), ScheduleActivity.class);
                        startActivity(intent);
                        break;

                    //room schedule
                    case 2:
                        intent.setClass(getApplicationContext(), RoomScheduleActivity.class);
                        startActivity(intent);
                        break;
                    //map
                    case 3:
                        intent.setClass(getApplicationContext(), MapActivity.class);
                        startActivity(intent);
                        break;

                    //Floor Guide
                    case 4:
                        intent.setClass(getApplicationContext(), FloorGuideActivity.class);
                        startActivity(intent);
                        break;

                    //Sponsor
                    case 5:
                        intent.setClass(getApplicationContext(), SponsorActivity.class);
                        startActivity(intent);
                        break;

                    //Notification
                    case 6:
                        intent.setClass(getApplicationContext(), NotificationActivity.class);
                        startActivity(intent);
                        break;
                    //About
                    case 7:
                        intent.setClass(getApplicationContext(), AboutActivity.class);
                        intent.putExtra("link", "about");
                        startActivity(intent);
                        break;

                    //Setting
                    case 8:
                        boolean loggedIn = mPreferencesManager.getBooleanPreference("LoggedIn",false);
                        Toast .makeText(HomeActivity.this, loggedIn + "", Toast.LENGTH_SHORT).show();
                        if(loggedIn == false) {
                            intent.setClass(getApplicationContext(), LoginActivity.class);
                        } else {
                            intent.setClass(getApplicationContext(), UserActivity.class);
                        }
                        startActivityForResult(intent, REQUEST_SIGN_IN);
                        break;
//                        intent.setClass(getApplicationContext(),PaperActivity.class);
//                        startActivity(intent);
//                        break;

                    // chat
                    case 9:
                        intent.setClass(getApplicationContext(), IMActivity.class);
                        startActivityForResult(intent, REQUEST_SIGN_IN);
                        break;
                    // nearby places
                    case 10:
                        intent.setClass(getApplicationContext(), NearbyActivity.class);
                        startActivity(intent);
                        break;
                    // author/sessions
                    case 11:
                        intent.setClass(getApplicationContext(), ModeratorActivity.class);
                        startActivity(intent);
                        break;
                    // networking
                    case 12:
                        intent.setClass(getApplicationContext(), NetworkingActivity.class);
                        startActivity(intent);
                        break;
                    // travel
                    case 13:
                        intent.setClass(getApplicationContext(), TravelAdvisorActivity.class);
                        startActivity(intent);
                        break;
                    // schedule data import/export, only admin user can see it
                    case 14:
                        intent.setClass(getApplicationContext(), TransferActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });

        // set up the nav drawer
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setTitle("Home");

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
        return new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,  R.string.drawer_close);
    }


    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SIGN_IN:
                finish();
                startActivity(getIntent());
                break;
        }
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

}
