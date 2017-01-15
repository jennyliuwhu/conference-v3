package cmu.cconfs.model.parseModel;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;

/**
 * Created by qiuzhexin on 12/27/16.
 *
 * (profile_img, bg_img, full_name, phone, email, compnay, title, description, user)
 */

@ParseClassName("Profile")
public class Profile extends ParseObject implements Serializable {
    public static String PIN_TAG = "profile_pin";

    public static String PROFILE_IMG_KEY = "profile_img";
    public static String BACKGROUND_IMG_KEY = "background_img";
    public static String FULL_NAME_KEY = "full_name";
    public static String PHONE_KEY = "phone";
    public static String EMAIL_KEY = "email";
    public static String COMPANY_KEY = "company";
    public static String TITLE_KEY = "title";
    public static String DESC_KEY = "description";
    public static String PARSE_USER_KEY = "parse_user";
    public static String SHARE_OPTION_KEY = "share_profile";

    public Profile() {

    }

    public ParseFile getProfileImage() {
        return getParseFile(PROFILE_IMG_KEY);
    }

    public void setProfileImage(ParseFile profImg) {
        put(PROFILE_IMG_KEY, profImg);
    }

    public ParseFile getBackgroundImage() {
        return getParseFile(BACKGROUND_IMG_KEY);
    }

    public void setBackgroundImage(ParseFile bgImg) {
        put(BACKGROUND_IMG_KEY, bgImg);
    }

    public String getFullName() {
        return getString(FULL_NAME_KEY);
    }

    public void setFullName(String n) {
        put(FULL_NAME_KEY, n);
    }

    public String getPhone() {
        return getString(PHONE_KEY);
    }

    public void setPhone(String p) {
        put(PHONE_KEY, p);
    }

    public String getEmail() {
        return getString(EMAIL_KEY);
    }

    public void setEmail(String e) {
        put(EMAIL_KEY, e);
    }

    public String getCompany() {
        return getString(COMPANY_KEY);
    }

    public void setCompany(String c) {
        put(COMPANY_KEY, c);
    }

    public String getTitle() {
        return getString(TITLE_KEY);
    }

    public void setTitle(String t) {
        put(TITLE_KEY, t);
    }

    public String getDescription() {
        return getString(DESC_KEY);
    }

    public void setDescription(String desc) {
        put(DESC_KEY, desc);
    }

    public ParseUser getParseUser() {
        return getParseUser(PARSE_USER_KEY);
    }

    public void setParseUser(ParseUser user) {
        put(PARSE_USER_KEY, user);
    }

    public Boolean getShareOption() {
        return getBoolean(SHARE_OPTION_KEY);
    }

    public void setShareOption(boolean share) {
        put(SHARE_OPTION_KEY, share);
    }

    public static ParseQuery<Profile> getQuery() {
        ParseQuery<Profile> query = ParseQuery.getQuery(Profile.class);
        query.setLimit(1000);
        return query;
    }

}
