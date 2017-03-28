package cmu.cconfs.parseUtils.helper;

import android.support.annotation.NonNull;

import com.google.gson.*;

import org.json.JSONException;
import org.json.JSONObject;

import cmu.cconfs.model.parseModel.FutureWeather;

/**
 * @author jialingliu
 */
public class JSONFutureWeatherParser {
    private JSONObject getJsonFutureWeather(@NonNull String data,
                                            long durationInS) {
        // response.json
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(data);
        JsonArray weathers = jsonElement.getAsJsonArray();
        System.out.println(weathers);
        long lowerTmp = Long.MAX_VALUE;
        JSONObject prev = null;
        try {
            for (int i = 0; i < weathers.size(); i++) {
                // do not use method jsonElement.getAsString()
                // it will throw UnsupportedOperationException
                JSONObject jsonObject = new JSONObject(weathers.get(i).toString());
                long epochDateTime = jsonObject.getLong("EpochDateTime");
                if ((i == 0 && epochDateTime < durationInS)
                        || i == weathers.size() - 1 && epochDateTime > durationInS){
                    return jsonObject;
                }
                if (durationInS > lowerTmp && durationInS < epochDateTime) {
                    if (durationInS - lowerTmp < epochDateTime - durationInS) {
                        return prev;
                    } else {
                        return jsonObject;
                    }
                } else if (durationInS == epochDateTime) {
                    return jsonObject;
                } else {
                    lowerTmp = epochDateTime;
                    prev = jsonObject;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return null;

    }
    public FutureWeather getFutureWeather(@NonNull String data,
                                          long durationInS) {
        // TODO: 3/24/17 parse futureWeather from response
        JSONObject jsonObject = getJsonFutureWeather(data, durationInS);
        if (jsonObject == null) {

        }
        return null;
    }
}
