package cmu.cconfs.model.parseModel;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by zmhbh on 8/24/15.
 */

@ParseClassName("Paper")
public class Paper extends ParseObject {
    public static final String PIN_TAG = "ALL_PAPERS";

    public String getAbstract() {
        return getString("abstract") == null ? "" : getString("abstract");
    }

    public void setAbstract(String s) {
        put("abstract", s);
    }

    public String getUniqueId() {
        return getString("unique_id") == null ? "" : getString("unique_id");
    }

    public void setUniqueId(String id) {
        put("unique_id", id);
    }

    public String getAuthor() {
        return getString("author") == null ? "" : getString("author");
    }

    public void setAuthor(String author) {
        put("author", author);
    }

    public int getPaperId() {
        return getInt("paper_id");
    }

    public void setPaperId(int id) {
        put("paper_id", id);
    }

    public String getAffiliation() {
        return getString("affiliation") == null ? "" : getString("affiliation");
    }

    public void setAffiliation(String a) {
        put("affiliation", a);
    }

    public String getTitle() {
        return getString("title") == null ? "" : getString("title");
    }

    public void setTitle(String t) {
        put("title", t);
    }

    public String getAuthorWithAffiliation() {
        return getString("authorwithaffiliation") == null ? "" : getString("authorwithaffiliation");
    }

    public void setAuthorWithAffiliation(String aa) {
        put("authorwithaffiliation", aa);
    }

    public static ParseQuery<Paper> getQuery() {
        ParseQuery<Paper> paperParseQuery= ParseQuery.getQuery(Paper.class);
        paperParseQuery.setLimit(1000);
        return paperParseQuery;
    }
}
