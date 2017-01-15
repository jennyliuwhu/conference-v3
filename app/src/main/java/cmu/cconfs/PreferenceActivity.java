package cmu.cconfs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import cmu.cconfs.fragment.PreferenceFragment;
import cmu.cconfs.model.parseModel.Profile;
import cmu.cconfs.model.parseModel.Program;
import cmu.cconfs.utils.PreferencesManager;

public class PreferenceActivity extends AppCompatActivity {
    public static String SHARE_PROFILE_PREF_KEY = CConfsApplication.getInstance().getString(R.string.share_profile_key);
    public static String NOTIFY_APPOINTMENT_PREF_KEY = CConfsApplication.getInstance().getString(R.string.appointment_key);
    public static String NOTIFY_MESSAGING_PREF_KEY = CConfsApplication.getInstance().getString(R.string.message_key);
    public static String SYNC_CALENDAR_PREF_KEY = CConfsApplication.getInstance().getString(R.string.calendar_key);

    private final static String TAG = PreferenceActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private PreferencesManager mPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPreferencesManager = new PreferencesManager(this, true);

        if (savedInstanceState == null) {
            Fragment preferenceFragment = new PreferenceFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.pref_container, preferenceFragment);
            ft.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUserPreference();
    }

    private void updateUserPreference() {
        final boolean shareProf = mPreferencesManager.getBooleanPreference(SHARE_PROFILE_PREF_KEY, true);
        boolean notifyAppointment = mPreferencesManager.getBooleanPreference(NOTIFY_APPOINTMENT_PREF_KEY, true);
        boolean notifyMsg = mPreferencesManager.getBooleanPreference(NOTIFY_MESSAGING_PREF_KEY, true);
        boolean syncCalendar = mPreferencesManager.getBooleanPreference(SYNC_CALENDAR_PREF_KEY, true);
        String prefSummary = String.format("share profile: %s\nnotify appointments: %s\nnotify messages: %s\nsync calendar: %s\n", shareProf, notifyAppointment, notifyMsg, syncCalendar);
        Log.d(TAG, "Pref summary:\n" + prefSummary);

        // update user's share profile option in the cloud
        ParseQuery<Profile> query = Profile.getQuery();
        query.whereEqualTo(Profile.PARSE_USER_KEY, ParseUser.getCurrentUser());
        query.getFirstInBackground(new GetCallback<Profile>() {
            @Override
            public void done(Profile profile, ParseException e) {
                profile.setShareOption(shareProf);
                profile.saveInBackground();
            }
        });
    }
}
