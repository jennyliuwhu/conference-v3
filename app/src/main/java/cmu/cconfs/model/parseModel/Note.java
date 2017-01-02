package cmu.cconfs.model.parseModel;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by qiuzhexin on 1/1/17.
 */

@ParseClassName("Note")
public class Note extends ParseObject {

    public static String SESSION_PIN_TAG = "session-note";
    public static String PAPER_PIN_TAG = "paper-note";

    public ParseUser getAuthor() {
        return getParseUser("author");
    }

    public void setAuthor(ParseUser user) {
        put("author", user);
    }

    public String getContent() {
        return getString("content");
    }

    public void setContent(String content) {
        put("content", content);
    }

    public String getSessionInfo() {
        return getString("session_info");
    }

    public void setSessionInfo(String key) {
        put("session_info", key);
    }

    public String getPaperInfo() {
        return getString("paper_info");
    }

    public void setPaperInfo(String key) {
        put("paper_info", key);
    }

    public static ParseQuery<Note> getQuery() {
        return ParseQuery.getQuery(Note.class);
    }
}
