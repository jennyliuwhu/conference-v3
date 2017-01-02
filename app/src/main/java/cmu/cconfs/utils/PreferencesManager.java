package cmu.cconfs.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;

import cmu.cconfs.R;

/**
 * Created by Wendy_Guo on 10/24/15.
 */
public class PreferencesManager {
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public PreferencesManager(Context context){
        sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public PreferencesManager(Context context, boolean getDefault) {
        if (getDefault) {
            sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            editor = sharedPref.edit();
        } else {
            sharedPref = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            editor = sharedPref.edit();
        }
    }

    public boolean getBooleanPreference(String key, boolean defaultValue){
        return sharedPref.getBoolean(key,defaultValue);
    }

    public String getStringPreference(String key, String defaultValue) {
        return sharedPref.getString(key, defaultValue);
    }
    public void writeIntPreference(String key, int value){
        editor.putInt(key, value);
        editor.commit();
    }
    public void writeBooleanPreference(String key, boolean value){
        editor.putBoolean(key, value);
        editor.commit();
    }
    public void writeStringPreference(String key, String value){
        editor.putString(key, value);
        editor.commit();
    }


}