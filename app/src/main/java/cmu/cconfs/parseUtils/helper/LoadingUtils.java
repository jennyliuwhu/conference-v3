package cmu.cconfs.parseUtils.helper;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import cmu.cconfs.CConfsApplication;
import cmu.cconfs.model.parseModel.Dirty;
import cmu.cconfs.model.parseModel.FloorPlan;
import cmu.cconfs.model.parseModel.Paper;
import cmu.cconfs.model.parseModel.Program;
import cmu.cconfs.model.parseModel.Room;
import cmu.cconfs.model.parseModel.Session_Room;
import cmu.cconfs.model.parseModel.Session_Timeslot;
import cmu.cconfs.model.parseModel.Sponsor;
import cmu.cconfs.model.parseModel.Timeslot;
import cmu.cconfs.model.parseModel.Version;
import cmu.cconfs.utils.data.DataProvider;
import cmu.cconfs.utils.data.RoomProvider;

/**
 * Created by qiuzhexin on 12/19/16.
 */

public class LoadingUtils {


    public static void loadFromParse() throws ParseException {
        ParseQuery paperQuery = Paper.getQuery();
        ParseQuery programQuery = Program.getQuery();
        ParseQuery roomQuery = Room.getQuery();
        ParseQuery sessionRoomQuery = Session_Room.getQuery();
        ParseQuery sessionTimeslotQuery = Session_Timeslot.getQuery();
        ParseQuery timeslotQuery = Timeslot.getQuery();
        ParseQuery versionQuery = Version.getQuery();
        ParseQuery floorPlanQuery = FloorPlan.getQuery();
        ParseQuery sponsorQuery = Sponsor.getQuery();

        ParseObject.unpinAll(Paper.PIN_TAG);
        ParseObject.unpinAll(Program.PIN_TAG);
        ParseObject.unpinAll(Room.PIN_TAG);
        ParseObject.unpinAll(Session_Room.PIN_TAG);
        ParseObject.unpinAll(Session_Timeslot.PIN_TAG);
        ParseObject.unpinAll(Timeslot.PIN_TAG);
        ParseObject.unpinAll(FloorPlan.PIN_TAG);
        ParseObject.unpinAll(Version.PIN_TAG);
        ParseObject.unpinAll(Sponsor.PIN_TAG);

        ParseObject.pinAll(Paper.PIN_TAG, paperQuery.find());
        ParseObject.pinAll(Program.PIN_TAG, programQuery.find());
        ParseObject.pinAll(Room.PIN_TAG, roomQuery.find());
        ParseObject.pinAll(Session_Room.PIN_TAG, sessionRoomQuery.find());
        ParseObject.pinAll(Session_Timeslot.PIN_TAG, sessionTimeslotQuery.find());
        ParseObject.pinAll(Timeslot.PIN_TAG, timeslotQuery.find());
        ParseObject.pinAll(FloorPlan.PIN_TAG, floorPlanQuery.find());
        ParseObject.pinAll(Sponsor.PIN_TAG, sponsorQuery.find());
        ParseObject.pinAll(Version.PIN_TAG, versionQuery.find());

    }

    public static void populateDataProvider() {
        CConfsApplication application = new CConfsApplication();
        DataProvider dataProvider = new DataProvider();
        application.setDataProvider(dataProvider);
    }

    public static void populateRoomProvider() {
        CConfsApplication application = new CConfsApplication();
        RoomProvider roomProvider = new RoomProvider();
        application.setRoomProvider(roomProvider);
    }
}
