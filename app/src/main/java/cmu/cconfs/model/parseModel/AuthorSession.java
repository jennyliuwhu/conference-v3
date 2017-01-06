package cmu.cconfs.model.parseModel;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by qiuzhexin on 1/4/17.
 */
@ParseClassName("AuthorSession")
public class AuthorSession extends ParseObject {

    public static final String PIN_TAG = "AUTHOR_SESSIONS";

    public int getAuthorId() {
        return getInt("author_id");
    }

    public void setAuthorId(int id) {
        put("author_id", id);
    }

    public String getAuthor() {
        return getString("author");
    }

    public void setAuthor(String author) {
        put("author", author);
    }

    public String getSessionIds() {
        return getString("session_ids");
    }

    public void setSessionIds(String ids) {
        put("session_ids", ids);
    }

    public static ParseQuery<AuthorSession> getQuery() {
        ParseQuery<AuthorSession> query = ParseQuery.getQuery(AuthorSession.class);
        query.setLimit(1000);
        return query;
    }
}
