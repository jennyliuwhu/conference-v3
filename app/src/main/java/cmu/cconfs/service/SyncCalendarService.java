package cmu.cconfs.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import cmu.cconfs.CConfsApplication;
import cmu.cconfs.PreferenceActivity;
import cmu.cconfs.utils.PreferencesManager;
import cmu.cconfs.utils.data.AbstractExpandableDataProvider;
import cmu.cconfs.utils.data.DataProvider;
import cmu.cconfs.utils.data.DayTriple;
import cmu.cconfs.utils.data.UnityDataProvider;

/**
 * Created by qiuzhexin on 1/3/17.
 */

public class SyncCalendarService extends IntentService {
    private final static String TAG = SyncCalendarService.class.getSimpleName();
    private final static int REMINDER_MINUTES = 15;

    public SyncCalendarService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PreferencesManager preferencesManager = new PreferencesManager(this, true);
        boolean syncCal = preferencesManager.getBooleanPreference(PreferenceActivity.SYNC_CALENDAR_PREF_KEY, true);
        if (syncCal) {
            syncCalendar();
        }
    }

    private void syncCalendar() {
        Log.d(TAG, "Syncing the calendar....");
        CConfsApplication app = CConfsApplication.getInstance();
        // clear the calendar
        app.clearCalendar();

        // add all the event and reminders
        for (int i = 0; i < DataProvider.days; i++) {
            UnityDataProvider provider = app.getUnityDataProvider(i);
            for (int j = 0; j < provider.getSelectedGroupCount(); j++) {
                AbstractExpandableDataProvider.BaseData groupData = provider.getSelectedGroupItem(j);
                String timeslot = groupData.getText();
                for (int k = 0; k < provider.getSelectedChildCount(j); k++) {
                    DayTriple triple = DataProvider.DATES[i];
                    UnityDataProvider.ConcreteChildData childData = provider.getSelectedChildItem(j, k);
                    String title = childData.getFirstText();
                    String room = childData.getSecondText();
                    // add the event to calendar
                    long eventId = app.addEvent(triple, timeslot, title, room);
                    // add reminder to the created event\
                    app.addReminder(eventId, REMINDER_MINUTES);
                }
            }
        }

    }
}
