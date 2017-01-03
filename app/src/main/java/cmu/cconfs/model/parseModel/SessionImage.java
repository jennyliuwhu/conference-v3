package cmu.cconfs.model.parseModel;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by qiuzhexin on 1/3/17.
 */

@ParseClassName("SessionImage")
public class SessionImage extends ParseObject {

    public static String SESSION_PIN_TAG = "session-image";
    public static String PAPER_PIN_TAG = "paper-image";

    public ParseUser getAuthor() {
        return getParseUser("author");
    }

    public void setAuthor(ParseUser user) {
        put("author", user);
    }

    public String getImagePaths() {
        return getString("image_paths");
    }

    public void setImagePaths(String paths) {
        put("image_paths", paths);
    }

    public String getSessionKey() {
        return getString("session_key");
    }

    public void setSessionKey(String key) {
        put("session_key", key);
    }

    public String getPaperKey() {
        return getString("paper_key");
    }

    public void setPaperKey(String key) {
        put("paper_key", key);
    }

    public static ParseQuery<SessionImage> getQuery() {
        return ParseQuery.getQuery(SessionImage.class);
    }
}
