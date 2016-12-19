package cmu.cconfs;

import android.app.Application;
import android.content.Context;

import com.easemob.EMCallBack;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.interceptors.ParseLogInterceptor;

import cmu.cconfs.instantMessage.IMHXSDKHelper;
import cmu.cconfs.model.parseModel.Sponsor;
import cmu.cconfs.model.Todo;
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
import cmu.cconfs.model.parseModel.Version;
import cmu.cconfs.utils.data.DataProvider;
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
        ParseObject.registerSubclass(Todo.class);

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
     * @param user
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
}
