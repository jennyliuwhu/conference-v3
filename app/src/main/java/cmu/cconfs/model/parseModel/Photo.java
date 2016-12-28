package cmu.cconfs.model.parseModel;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**m
 * Created by mangobin on 15/10/20.
 */
@ParseClassName("Photo")
public class Photo extends ParseObject {
    public Photo() {
    }

    public String getSessionKey() {
        return getString("sessionKey");
    }

    public void setSessionKey(String name) {
        put("sessionKey", name);
    }

    public ParseFile getPhotoFile() {
        return getParseFile("photoName");
    }

    public void setPhotoFile(ParseFile file) {
        put("photoName", file);
    }

    public void setPublisher(String publisher) {
        put("publisher", publisher);
    }

    public String getPublisher() {
        return getString("publisher");
    }

    public List<Photo> getPhotoQueryBySessionKey(String key) {
        ParseQuery<Photo> query = ParseQuery.getQuery(Photo.class);
        query.whereEqualTo("sessionKey", key);
        try {
            return query.find();

        } catch (ParseException e) {
            Log.e("Photo", "Error: " + query.count() + e.getMessage());
        } finally {
            return null;
        }
    }

    class QueryCallBack implements FindCallback<Photo> {
        List<Photo> ret = new ArrayList<Photo>();

        public void done(List<Photo> photoList, ParseException e) {
            if (e == null) {
                for (Photo photo : photoList) {
                    ret.add(photo);
                }
            } else {
                Log.e("Photo Error", "Error: " + e.getMessage());
            }
        }

        public List<Photo> getPhotos() {
            return ret;
        }
    }
}
