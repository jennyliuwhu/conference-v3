package cmu.cconfs;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Reminders;

import com.easemob.EMCallBack;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.interceptors.ParseLogInterceptor;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import cmu.cconfs.instantMessage.IMHXSDKHelper;
import cmu.cconfs.model.parseModel.AuthorSession;
import cmu.cconfs.model.parseModel.Note;
import cmu.cconfs.model.parseModel.Profile;
import cmu.cconfs.model.parseModel.SessionImage;
import cmu.cconfs.model.parseModel.Sponsor;
import cmu.cconfs.model.parseModel.FloorPlan;
import cmu.cconfs.model.parseModel.Message;
import cmu.cconfs.model.parseModel.Paper;
import cmu.cconfs.model.parseModel.Photo;
import cmu.cconfs.model.parseModel.Program;
import cmu.cconfs.model.parseModel.Rate;
import cmu.cconfs.model.parseModel.Room;
import cmu.cconfs.model.parseModel.Session_Room;
import cmu.cconfs.model.parseModel.Session_Timeslot;
import cmu.cconfs.model.parseModel.Timeslot;
import cmu.cconfs.model.parseModel.Todo;
import cmu.cconfs.model.parseModel.TodoCached;
import cmu.cconfs.model.parseModel.Version;
import cmu.cconfs.utils.data.DataProvider;
import cmu.cconfs.utils.data.DayTriple;
import cmu.cconfs.utils.data.RoomDataProvider;
import cmu.cconfs.utils.data.RoomProvider;
import cmu.cconfs.utils.data.UnityDataProvider;

/**
 * Created by zmhbh on 8/23/15.
 */
public class CConfsApplication extends Application {
    public static final String TODO_GROUP_NAME = "ALL_TODOS";

    private static DataProvider dataProvider;
    private static RoomProvider roomProvider;
    private static boolean isDataUpToDate;

    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public void setRoomProvider(RoomProvider roomProvider) {
        this.roomProvider = roomProvider;
    }

    public UnityDataProvider getUnityDataProvider(int dateIndex) {
        return dataProvider.getUnityDataProvider(dateIndex);
    }

    public RoomDataProvider getRoomDataProvider(int roomIndex) {
        return roomProvider.getRoomDataProvider(roomIndex);
    }

    //    instant message
    public static Context applicationContext;
    private static CConfsApplication instance;
    // login user name
    public final String PREF_USERNAME = "username";

    /**
     * 当前用户nickname,为了苹果推送不是userid而是昵称
     */
    public static String currentUserNick = "";
    public static IMHXSDKHelper hxSDKHelper = new IMHXSDKHelper();

    private static final String ParseAppID = "UUL8TxlHwKj7ZXEUr2brF3ydOxirCXdIj9LscvJs";
    private static final String ParseClientKey = "B1jH9bmxuYyTcpoFfpeVslhmLYsytWTxqYqKQhBJ";

    @Override
    public void onCreate() {
        super.onCreate();

        // facebook image library
        Fresco.initialize(this);

        // add Todo subclass

        ParseObject.registerSubclass(Paper.class);
        ParseObject.registerSubclass(Program.class);
        ParseObject.registerSubclass(Room.class);
        ParseObject.registerSubclass(Session_Room.class);
        ParseObject.registerSubclass(Session_Timeslot.class);
        ParseObject.registerSubclass(Timeslot.class);
        ParseObject.registerSubclass(Version.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Photo.class);
        ParseObject.registerSubclass(Rate.class);
        ParseObject.registerSubclass(FloorPlan.class);
        ParseObject.registerSubclass(Sponsor.class);
        ParseObject.registerSubclass(Profile.class);
        ParseObject.registerSubclass(Todo.class);
        ParseObject.registerSubclass(TodoCached.class);
        ParseObject.registerSubclass(Note.class);
        ParseObject.registerSubclass(SessionImage.class);
        ParseObject.registerSubclass(AuthorSession.class);

        // set application id and connect to server
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.parse_app_id))
                .clientKey(null)
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server(getString(R.string.parse_server_endpoint)).enableLocalDataStore().build());

//        Parse.initialize(this,ParseAppID,ParseClientKey);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground("CConfs");

        ParseUser.enableAutomaticUser();
        ParseUser.enableRevocableSessionInBackground();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

//        instant message
        applicationContext = this;
        instance = this;
        hxSDKHelper.onInit(applicationContext);
    //    SDKInitializer.initialize(getApplicationContext());

        if (getCalendarId() == -1) {
            createCalendar();
        }

//        clearCalendar();
//        long eventId = addEvent(new DayTriple(2017, 0, 3), "20:00-21:30", "test title", "1021");
//        addReminder(eventId, 15);
    }

    public static CConfsApplication getInstance() {
        return instance;
    }


    /**
     * 获取当前登陆用户名
     *
     * @return
     */
    public String getUserName() {
        return hxSDKHelper.getHXId();
    }

    /**
     * 获取密码
     *
     * @return
     */
    public String getPassword() {
        return hxSDKHelper.getPassword();
    }

    /**
     * 设置用户名
     *
     * @param username
     */
    public void setUserName(String username) {
        hxSDKHelper.setHXId(username);
    }

    /**
     * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
     * 内部的自动登录需要的密码，已经加密存储了
     *
     * @param pwd
     */
    public void setPassword(String pwd) {
        hxSDKHelper.setPassword(pwd);
    }

    /**
     * 退出登录,清空数据
     */
    public void logout(final boolean isGCM, final EMCallBack emCallBack) {
        // 先调用sdk logout，在清理app中自己的数据
        hxSDKHelper.logout(isGCM, emCallBack);
    }


    public void setDataStatus(boolean status) {
        isDataUpToDate = status;
    }

    public boolean getDataStatus() {
        return isDataUpToDate;
    }

    /* app calendar feature */
    private final static String CITY_LOCATION = "Honolulu, Hawaii, USA";
    private final static String CITY_TIMEZONE = "America/Los_Angeles";
    private final static String MY_ACCOUNT_NAME = "scconfs-calendar-account";
    private final static String MY_EMAIL_ACCOUNT = "icwscmu@gmail.com";
    private final static String ORGANIZER_EMAIL = "some.mail@some.address.com";


    private void createCalendar() {
        ContentValues values = new ContentValues();
        values.put(
                Calendars.ACCOUNT_NAME,
                MY_ACCOUNT_NAME);
        values.put(
                Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(
                Calendars.NAME,
                "SCConfs Android Calendar");
        values.put(
                Calendars.CALENDAR_DISPLAY_NAME,
                "SCConfs Android Calendar");
        values.put(
                Calendars.CALENDAR_COLOR,
                0xffff0000);
        values.put(
                Calendars.CALENDAR_ACCESS_LEVEL,
                Calendars.CAL_ACCESS_OWNER);
        values.put(
                Calendars.OWNER_ACCOUNT,
                MY_EMAIL_ACCOUNT);
        values.put(
                Calendars.CALENDAR_TIME_ZONE,
                "America/Los_Angeles");
        values.put(
                Calendars.SYNC_EVENTS,
                1);
        Uri.Builder builder =
                CalendarContract.Calendars.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(
                Calendars.ACCOUNT_NAME,
                getPackageName());
        builder.appendQueryParameter(
                Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(
                CalendarContract.CALLER_IS_SYNCADAPTER,
                "true");
        Uri uri = getContentResolver().insert(builder.build(), values);
    }

    private long getCalendarId() throws SecurityException {
        String[] projection = new String[]{CalendarContract.Calendars._ID};
        String selection =
                CalendarContract.Calendars.ACCOUNT_NAME +
                        " = ? AND " +
                        CalendarContract.Calendars.ACCOUNT_TYPE +
                        " = ? ";
        // use the same values as above:
        String[] selArgs = new String[]{ MY_ACCOUNT_NAME, CalendarContract.ACCOUNT_TYPE_LOCAL};
        Cursor cursor = getContentResolver().query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                selection,
                selArgs,
                null);
        if (cursor.moveToFirst()) {
            return cursor.getLong(0);
        }
        return -1;
    }

    // timeslot example: 12:00-3:00
    public long addEvent(DayTriple triple, String timeslot, String title, String room) throws SecurityException {
        long calId = getCalendarId();
        if (calId == -1) {
            // no calendar account; react meaningfully
            return -1;
        }

        // event day
        Calendar cal = new GregorianCalendar(triple.year, triple.month, triple.dayInMonth);
        String[] timeslot_start_end = timeslot.split("-");
        String[] hour_minute_start = timeslot_start_end[0].split(":");
        String[] hour_minute_end = timeslot_start_end[1].split(":");

        // event start time
        cal.setTimeZone(Calendar.getInstance().getTimeZone());
        cal.set(Calendar.HOUR, Integer.parseInt(hour_minute_start[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(hour_minute_start[1]));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long start = cal.getTimeInMillis();

        // event end time
        cal = new GregorianCalendar(triple.year, triple.month, triple.dayInMonth);
        cal.setTimeZone(Calendar.getInstance().getTimeZone());
        cal.set(Calendar.HOUR, Integer.parseInt(hour_minute_end[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(hour_minute_end[1]));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long end = cal.getTimeInMillis();

        // enter calendar data
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, start);
        values.put(CalendarContract.Events.DTEND, end);
//        values.put(Events.RRULE, "FREQ=DAILY;COUNT=20;BYDAY=MO,TU,WE,TH,FR;WKST=MO");
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.EVENT_LOCATION,  CITY_LOCATION);
        values.put(CalendarContract.Events.CALENDAR_ID, calId);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, CITY_TIMEZONE);
        values.put(CalendarContract.Events.DESCRIPTION, "Room: " + room);

        // reasonable defaults exist:
        values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
        values.put(CalendarContract.Events.SELF_ATTENDEE_STATUS, CalendarContract.Events.STATUS_CONFIRMED);
//        values.put(CalendarContract.Events.ALL_DAY, 1);
        values.put(CalendarContract.Events.ORGANIZER, ORGANIZER_EMAIL);
        values.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 1);
        values.put(CalendarContract.Events.GUESTS_CAN_MODIFY, 1);
        values.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        Uri uri = getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);

        // return event id
        return new Long(uri.getLastPathSegment());
    }

    public long addReminder(long eventId, int minutes) throws SecurityException {
        ContentValues values = new ContentValues();
        values.put(Reminders.EVENT_ID, eventId);
        values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
        values.put(Reminders.MINUTES, minutes);
        Uri uri = getContentResolver().insert(Reminders.CONTENT_URI, values);

        // return reminder id
        return new Long(uri.getLastPathSegment());
    }

    public void clearCalendar() {
        Uri eventsUri;
        int osVersion = android.os.Build.VERSION.SDK_INT;
        if (osVersion <= 7) { //up-to Android 2.1
            eventsUri = Uri.parse("content://calendar/events");
        } else { //8 is Android 2.2 (Froyo) (http://developer.android.com/reference/android/os/Build.VERSION_CODES.html)
            eventsUri = Uri.parse("content://com.android.calendar/events");
        }
        ContentResolver resolver = this.getContentResolver();
        deleteEvent(resolver, eventsUri, getCalendarId());
    }

    private void deleteEvent(ContentResolver resolver, Uri eventsUri, long calendarId) {
        Cursor cursor;
        if (android.os.Build.VERSION.SDK_INT <= 7) { //up-to Android 2.1
            cursor = resolver.query(eventsUri, new String[]{ "_id" }, "Calendars._id=" + calendarId, null, null);
        } else { //8 is Android 2.2 (Froyo) (http://developer.android.com/reference/android/os/Build.VERSION_CODES.html)
            cursor = resolver.query(eventsUri, new String[]{ "_id" }, "calendar_id=" + calendarId, null, null);
        }
        while(cursor.moveToNext()) {
            long eventId = cursor.getLong(cursor.getColumnIndex("_id"));
            // delete any associated reminders
            deleteReminders(eventId, resolver);
            // delete the event
            resolver.delete(ContentUris.withAppendedId(eventsUri, eventId), null, null);
        }
        cursor.close();
    }

    private void deleteReminders(long eventId, ContentResolver resolver) {
        String[] projection = new String[] {
                CalendarContract.Reminders._ID,
        };

        Cursor cursor = CalendarContract.Reminders.query(resolver, eventId, projection);
        while (cursor.moveToNext()) {
            long reminderId = cursor.getLong(0);
            Uri uri = ContentUris.withAppendedId(CalendarContract.Reminders.CONTENT_URI, reminderId);
            int rows = resolver.delete(uri, null, null);
        }
        cursor.close();
    }



}
