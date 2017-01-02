package cmu.cconfs.fragment;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cmu.cconfs.R;


/**
 * Created by qiuzhexin on 1/1/17.
 */

public class PreferenceFragment extends PreferenceFragmentCompat {

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_preference, container, false);
//    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.app_preferences);
    }
}
