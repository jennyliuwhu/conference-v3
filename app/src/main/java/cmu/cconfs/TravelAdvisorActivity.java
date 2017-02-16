package cmu.cconfs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.parse.ParseException;

import cmu.cconfs.parseUtils.helper.LoadingUtils;
import cmu.cconfs.utils.PreferencesManager;

/**
 * @author jialingliu
 */
public class TravelAdvisorActivity extends AppCompatActivity {
    private WebView webView;
    public static PreferencesManager mPreferencesManager;

    // set up drawer
    private static final String TAG = TravelAdvisorActivity.class.getName();
    public final static int REQUEST_SIGN_IN = 1;

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);
        mPreferencesManager = new PreferencesManager(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setTitle("Travel Advisor");

        webView = (WebView) findViewById(R.id.webView1);
//        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadDataWithBaseURL(null, "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<body>\n" +
                "    <p>I'm your travel advisor</p>\n" +
                "</body>\n" +
                "</html>", "text/html", "utf-8", null);
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
}
