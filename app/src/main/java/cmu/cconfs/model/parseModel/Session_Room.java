package cmu.cconfs.model.parseModel;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by zmhbh on 8/24/15.
 */
@ParseClassName("Session_Room")
public class Session_Room extends ParseObject {

    public static final String PIN_TAG = "ALL_SESSION_ROOMS";

    public String getChair() {
        return getString("chair") == null ? "" : getString("chair");
    }

    public void setChair(String c) {
        put("chair", c);
    }

    public String getPapers() {
        return getString("papers") == null ? "" : getString("papers");
    }

    public void setPapers(String ps) {
        put("papers", ps);
    }

    public String getTimeslot() {
        return getString("timeslot") == null ? "" : getString("timeslot");
    }

    public void setTimeslot(String t) {
        put("timeslot", t);
    }

    public String getSessionTitle() {
        return getString("session_title") == null ? "" : getString("session_title");
    }

    public void setSessionTitle(String st) {
        put("session_title", st);
    }

    public String getSessionName() {
        return getString("value") == null ? "" : getString("value");
    }

    public void setSessionName(String v) {
        put("value", v);
    }

    public int getRoomId() {
        return getInt("room_id");
    }

    public void setRoomId(int id) {
        put("room_id", id);
    }

    public int getSelected() {
        return getInt("selected");
    }

    public void setSelected(int selected) {
        put("selected", selected);
    }

    public void setSessionId(int id) {
        put("session_id", id);
    }

    public int getSessionId() {
        return getInt("session_id");
    }

    public static ParseQuery<Session_Room> getQuery() {
        ParseQuery<Session_Room> parseQuery = ParseQuery.getQuery(Session_Room.class);
        parseQuery.setLimit(1000);
        return parseQuery;
    }
}
