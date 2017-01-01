package cmu.cconfs.model.parseModel;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by qiuzhexin on 1/1/17.
 */

@ParseClassName("TodoCached")
public class TodoCached extends ParseObject {

    public ParseUser getAuthor() {
        return getParseUser("author");
    }

    public void setAuthor(ParseUser author) {
        put("author", author);
    }

    public static ParseQuery<TodoCached> getQuery() {
        return ParseQuery.getQuery(TodoCached.class);
    }
}
