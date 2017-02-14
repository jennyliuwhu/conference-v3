package cmu.cconfs.model.parseModel;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by mangobin on 15/12/7.
 */
@ParseClassName("FloorPlan")
public class FloorPlan extends ParseObject {
    public static final String PIN_TAG = "ALL_FLOORPLANS";

    public ParseFile getPhotoFile() {
        return getParseFile("photo");
    }

    public void setPhotoFile(ParseFile file) {
        put("photo", file);
    }

    public void setPhotoName(String photoName) {
        put("photoName", photoName);
    }

    public String getPhototName() {
        return getString("photoName");
    }

    public static ParseQuery<FloorPlan> getQuery() {
        return ParseQuery.getQuery(FloorPlan.class);
    }
}
