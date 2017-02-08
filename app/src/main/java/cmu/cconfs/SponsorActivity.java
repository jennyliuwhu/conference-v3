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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import cmu.cconfs.adapter.SponsorListAdapter;
import cmu.cconfs.model.parseModel.Sponsor;
import cmu.cconfs.parseUtils.helper.LoadingUtils;
import cmu.cconfs.utils.PreferencesManager;


public class SponsorActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getName();
    RecyclerView recyclerView;
    SponsorListAdapter adapter;

    List<Sponsor> sponsors = new ArrayList<>();
    public static PreferencesManager mPreferencesManager;

    public final static int REQUEST_SIGN_IN = 1;

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sponsor);
        mPreferencesManager = new PreferencesManager(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.inflateMenu(R.menu.menu_paper);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setDisplayUseLogoEnabled(false);
//        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Sponsor");

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new SponsorListAdapter(this.sponsors);
        recyclerView.setAdapter(adapter);

        populate();

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

    private void populate() {
        ParseQuery<Sponsor> query = Sponsor.getQuery();
        query.fromLocalDatastore();
        query.fromPin(Sponsor.PIN_TAG);
        List<Sponsor> sponsorList = null;
        try {
            sponsorList = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (Sponsor sponsor : sponsorList) {
            sponsors.add(sponsor);
        }
//        this.sponsors.add(new Sponsor("IEEE Technical", "http://tab.computer.org/tcsc/", "https://lh3.googleusercontent.com/-E7uX3DtJB-k/AAAAAAAAAAI/AAAAAAAAAAA/B_Bao68bswo/s0-c-k-no-ns/photo.jpg"));
//        this.sponsors.add(new Sponsor("IEEE Computer Society", "http://www.computer.org/web/guest", "https://lh6.googleusercontent.com/-l7sOVB2N87o/AAAAAAAAAAI/AAAAAAAAAAA/X8RO1wjyeRI/s0-c-k-no-ns/photo.jpg"));
//        this.sponsors.add(new Sponsor("ERICSSON", "http://www.ericsson.com", "https://lh5.googleusercontent.com/-LxR6S5MMeW0/AAAAAAAAAAI/AAAAAAAAAAA/ywVsXJf0dfo/s0-c-k-no-ns/photo.jpg"));
//        this.sponsors.add(new Sponsor("HP", "http://m.hp.com/us/en/home.html", "https://lh4.googleusercontent.com/-CKNnBHvWTN0/AAAAAAAAAAI/AAAAAAAAAAA/1yqw2lShG5o/s0-c-k-no-ns/photo.jpg"));
//        this.sponsors.add(new Sponsor("IBM", "http://m.ibm.com/us/en", "https://lh3.googleusercontent.com/-ApNLZ2_15_U/AAAAAAAAAAI/AAAAAAAAAAA/12GouyuqIwo/s0-c-k-no-ns/photo.jpg"));
//        this.sponsors.add(new Sponsor("SAP", "http://go.sap.com/index.html", "https://lh3.googleusercontent.com/-q2M7Q9v6gno/AAAAAAAAAAI/AAAAAAAAAAA/TguyrMPHUa8/s0-c-k-no-ns/photo.jpg"));
//        this.sponsors.add(new Sponsor("HUAWEI", "http://www.huawei.com/en", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Huawei.svg/1000px-Huawei.svg.png"));
//        this.sponsors.add(new Sponsor("OMG", "http://www.omg.org/", "https://upload.wikimedia.org/wikipedia/en/f/f1/OMG-logo.jpg"));
//        this.sponsors.add(new Sponsor("IBM Research", "http://www.research.ibm.com", "https://pbs.twimg.com/profile_images/2453018418/fn1i02hac59i02ccd9c1_400x400.jpeg"));
    }
}