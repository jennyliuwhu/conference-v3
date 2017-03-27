package cmu.cconfs.parseUtils.helper;

import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Parse location data from http://apidev.accuweather.com/locations/v1/
 *
 * @author jialingliu
 */
public class LocationParser {
    /**
     * Parse location data, i.e. get location Key from the first element of the json array
     * @param data from http://apidev.accuweather.com/locations/v1/search?q=san%20jose,United%20States&apikey=hoArfRosT1215
     * @return locationKey
     */
    public String getLocationKey(@NonNull String data) {
        // Parse location data
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(data);
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonArray.get(0).getAsString());
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("json transform failed");
        }
        if (jsonObject != null) {
            try {
                System.out.println(jsonObject.toString());
                return jsonObject.getString("Key");
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("get locationKey failed");
            }
        }
        // default value san jose, CA
        return "347630";
    }
}
