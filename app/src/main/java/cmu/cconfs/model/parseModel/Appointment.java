package cmu.cconfs.model.parseModel;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by qiuzhexin on 1/15/17.
 */

@ParseClassName("Appointment")
public class Appointment extends ParseObject {
    public static final String PIN_TAG = "APPOINTMENT";
    public static final String MY_USERNAME_KEY = "my_username";
    public static final String OTHER_USERNAME_KEY = "other_username";
    public static final String OTHER_USER_REAL_NAME = "other_realname";
    public static final String SUBJECT_KEY = "subject";
    public static final String TIME_KEY = "time";
    public static final String DETAIL_KEY = "detail";

    public String getMyUsername() {
        return getString(MY_USERNAME_KEY);
    }

    public void setMyUsername(String myUsername) {
        put(MY_USERNAME_KEY, myUsername);
    }

    public String getOtherUsername() {
        return getString(OTHER_USERNAME_KEY);
    }

    public void setOtherUsername(String otherUsername) {
        put(OTHER_USERNAME_KEY, otherUsername);
    }

    public String getSubject() {
        return getString(SUBJECT_KEY);
    }

    public void setSubject(String subject) {
        put(SUBJECT_KEY, subject);
    }

    public String getDetail() {
        return getString(DETAIL_KEY);
    }

    public void setDetail(String detail) {
        put(DETAIL_KEY, detail);
    }

    public String getTime() {
        return getString(TIME_KEY);
    }

    public void setTime(String time) {
        put(TIME_KEY, time);
    }

    public String getOtherRealName() {
        return getString(OTHER_USER_REAL_NAME);
    }

    public void setOtherRealName(String realName) {
        put(OTHER_USER_REAL_NAME, realName);
    }

    public static ParseQuery<Appointment> getQuery() {
        ParseQuery<Appointment> query = ParseQuery.getQuery(Appointment.class);
        query.setLimit(1000);
        return query;
    }
}
