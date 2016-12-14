package cmu.cconfs.model.parseModel;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by zmhbh on 8/24/15.
 */

@ParseClassName("Program")
public class Program extends ParseObject {

    public static final String PIN_TAG = "ALL_PROGRAMS";

    public void setProgramId(int id) {
        put("program_id", id);
    }

    public int getProgramId() {
        return getInt("program_id");
    }

    public String getDate() {
        return getString("value") == null ? "" : getString("value");
    }

    public void setDate(String d) {
        put("value", d);
    }

    public static ParseQuery<Program> getQuery() {
        return ParseQuery.getQuery(Program.class);
    }


}
