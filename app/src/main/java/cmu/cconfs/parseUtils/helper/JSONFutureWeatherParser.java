package cmu.cconfs.parseUtils.helper;

import android.support.annotation.NonNull;

import com.google.gson.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cmu.cconfs.model.parseModel.FutureWeather;

/**
 * @author jialingliu
 */
public class JSONFutureWeatherParser {
    public FutureWeather getFutureWeather(@NonNull String data,
                                          long durationInS) {
        // TODO: 3/24/17 parse futureWeather from response
        // response.json
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(data);
        JsonArray weathers = jsonElement.getAsJsonArray();
        System.out.println(weathers);
        List<Long> epochDateTimes = new ArrayList<>();
        try {
            for (int i = 0; i < weathers.size(); i++) {
                // do not use method jsonElement.getAsString()
                // it will throw UnsupportedOperationException
                JSONObject jsonObject = new JSONObject(weathers.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return null;
    }
}
