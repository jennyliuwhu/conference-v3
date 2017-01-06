package cmu.cconfs.parseUtils.helper;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cmu.cconfs.CConfsApplication;
import cmu.cconfs.model.parseModel.AuthorSession;
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
    private static final String TAG = "LoadingUtils";

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

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) CConfsApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // load author_sessions (author_id, author, session_ids)
    public static void populateAuthors() throws ParseException {
        Log.d(TAG, "Start loading authors....");
        ParseObject.unpinAll(AuthorSession.PIN_TAG);

        Map<String, Set<String>> paperIdToAuthorsMap = new HashMap<>();
        List<Paper> papers = Paper.getQuery().fromLocalDatastore().find();
        for (Paper p : papers) {
            String authorsStr = p.getAuthor().trim();
            String paperId = p.getUniqueId().trim();

            if (authorsStr.contains("，")) {
                authorsStr = authorsStr.replace("，", ",");
            }
            String[] authors = authorsStr.split(",");

            for (String author : authors) {
                author = author.trim();
                if (author.isEmpty()) {
                    continue;
                }
                if (!paperIdToAuthorsMap.containsKey(paperId)) {
                    paperIdToAuthorsMap.put(paperId, new HashSet<String>());
                }
                paperIdToAuthorsMap.get(paperId).add(author);
            }
        }
        Log.d(TAG, "paperToAuthorMap: \n" + paperIdToAuthorsMap.toString());

        Map<String, Set<Integer>> authorToSessionIdsMap = new HashMap<>();
        List<Session_Timeslot> sessions = Session_Timeslot.getQuery().fromLocalDatastore().find();
        for (Session_Timeslot st : sessions) {
            int sessionId = st.getSessionId();
            String paperIdsStr = st.getPapers().trim();

            if (paperIdsStr.contains("，")) {
                paperIdsStr = paperIdsStr.replace("，", ",");
            }

            String[] paperIds = paperIdsStr.split(",");
            for (String pid : paperIds) {
                pid = pid.trim();
                if (pid.isEmpty()) {
                    continue;
                }
                if (paperIdToAuthorsMap.containsKey(pid)) {
                    for (String author : paperIdToAuthorsMap.get(pid)) {
                        if (!authorToSessionIdsMap.containsKey(author)) {
                            authorToSessionIdsMap.put(author, new HashSet<Integer>());
                        }
                        authorToSessionIdsMap.get(author).add(sessionId);
                    }
                }
            }
        }
        Log.d(TAG, "authorToSessionIdsMap: \n" + authorToSessionIdsMap.toString());


        List<AuthorSession> authorSessions = new ArrayList<>();
        int authorId = 0;
        for (Map.Entry<String, Set<Integer>> entry : authorToSessionIdsMap.entrySet()) {
            String author = entry.getKey();
            StringBuffer sessionIdStr = new StringBuffer();
            for (int sessionId : entry.getValue()) {
                sessionIdStr.append(sessionId).append(",");
            }

            AuthorSession as = new AuthorSession();
            as.setAuthor(author);
            as.setAuthorId(authorId++);
            as.setSessionIds(sessionIdStr.toString());
            authorSessions.add(as);
        }

        ParseObject.pinAll(AuthorSession.PIN_TAG, authorSessions);

        Log.d(TAG, "Loaded " + authorSessions.size() + " authors");
    }

}
