package cmu.cconfs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.dk.view.folder.ResideMenu;
import com.dk.view.folder.ResideMenuItem;
import com.parse.ParseAnalytics;

import org.bitlet.weupnp.Main;

import cmu.cconfs.fragment.HomeFragment;
import cmu.cconfs.receiver.SyncCalendarAlarmReceiver;


public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    private ResideMenu resideMenu;

    private ResideMenuItem itemHome_left;
    private ResideMenuItem itemHome_right;
    private ResideMenuItem itemMySchedule_left;
    private ResideMenuItem itemMySchedule_right;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // start the syncing calendar service
        scheduleAlarm();

        // for analytics
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupMenu();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, new HomeFragment(), "fragment")
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }


    private void setupMenu() {
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.golden_gate_small);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.618f);

        //create Menu items;
        itemHome_left = new ResideMenuItem(this, R.drawable.ic_home_white_48dp, "Home");
        itemHome_right = new ResideMenuItem(this, R.drawable.ic_home_white_48dp, "Home");
         /*
         */
        itemMySchedule_left = new ResideMenuItem(this, R.drawable.ic_alarm_on_white_48dp, "My Schedule");
        itemMySchedule_right = new ResideMenuItem(this, R.drawable.ic_alarm_on_white_48dp, "My Schedule");

        itemHome_left.setOnClickListener(this);
        itemHome_right.setOnClickListener(this);
        itemMySchedule_left.setOnClickListener(this);
        itemMySchedule_right.setOnClickListener(this);

        resideMenu.addMenuItem(itemHome_left, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemMySchedule_left, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemHome_right, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemMySchedule_right, ResideMenu.DIRECTION_RIGHT);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //
        return resideMenu.dispatchTouchEvent(ev);
    }


    @Override
    public void onClick(View v) {

        if (v == itemHome_left || v == itemHome_right) {
            Intent intent = new Intent();
            intent.setClass(this, HomeActivity.class);

            startActivity(intent);
            resideMenu.closeMenu();
        } else {
            Intent intent = new Intent();
            intent.setClass(this, ScheduleActivity.class);

            startActivity(intent);
            resideMenu.closeMenu();
        }
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {

        }

        @Override
        public void closeMenu() {

        }
    };

    public ResideMenu getResideMenu() {
        return resideMenu;
    }


    // schedule alarms in half an hour interval
    private void scheduleAlarm() {
        Intent intent = new Intent(getApplicationContext(), SyncCalendarAlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, SyncCalendarAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, AlarmManager.INTERVAL_HALF_HOUR, pIntent);
        Log.d(TAG, "Schedule an alarm for syncing calendar");
    }

    // cancel the scheduled alarm
    private void cancelAlarm() {

        Intent intent = new Intent(getApplicationContext(), SyncCalendarAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, SyncCalendarAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "I am destroyed");
    }
}
