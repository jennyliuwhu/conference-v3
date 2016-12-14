package cmu.cconfs.model.parseModel;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by zmhbh on 8/24/15.
 */
@ParseClassName("Room")
public class Room extends ParseObject {

    public static final String PIN_TAG = "ALL_ROOMS";

    public void setRoomId(int id) {
        put("room_id", id);
    }

    public int getRoomId() {
        return getInt("room_id");
    }

    public void setProgramId(int programId) {
        put("program_id", programId);
    }

    public int getProgramId() {
        return getInt("program_id");
    }

    public void setValue(String value) {
        put("value", value);
    }

    public String getValue() {
        return getString("value");
    }

    public String getRoom() {
        return getString("value") == null ? "" : getString("value");
    }

    public void setRoom(String r) {
        put("value", r);
    }

    public static ParseQuery<Room> getQuery() {
        ParseQuery<Room> parseQuery = ParseQuery.getQuery(Room.class);
        parseQuery.setLimit(1000);
        return parseQuery;
    }
}
