package cmu.cconfs.model.parseModel;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.UUID;

@ParseClassName("Todo")
public class Todo extends ParseObject {

    public final static String PIN_TAG = "todo";
     
    public String getTitle() {
        return getString("title");
    }
     
    public void setTitle(String title) {
        put("title", title);
    }
     
    public ParseUser getAuthor() {
        return getParseUser("author");
    }
     
    public void setAuthor(ParseUser currentUser) {
        put("author", currentUser);
    }
     
    public boolean isDraft() {
        return getBoolean("isDraft");
    }
     
    public void setDraft(boolean isDraft) {
        put("isDraft", isDraft);
    }
     
    public void setUuidString() {
        UUID uuid = UUID.randomUUID();
        put("uuid", uuid.toString());
    }
     
    public String getUuidString() {
        return getString("uuid");
    }
     
    public static ParseQuery<Todo> getQuery() {
        return ParseQuery.getQuery(Todo.class);
    }
}