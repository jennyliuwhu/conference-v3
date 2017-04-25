package cmu.cconfs.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cmu.cconfs.model.parseModel.FutureWeather;
import cmu.cconfs.parseUtils.helper.JSONFutureWeatherParser;
import cmu.cconfs.parseUtils.helper.LocationParser;

/**
 * @author jialingliu
 */
public class WeatherHttpClient {
    private static final String GET_KEY_URL = "http://apidev.accuweather.com/locations/v1/search?q=%s&apikey=hoArfRosT1215";
    private static final String BASE_URL = "http://dataservice.accuweather.com/forecasts/v1/hourly/12hour/%s?apikey=%s&details=true&metric=true";
    private static final String apiKey = "2HbZMIUw4YGYi1GQ9km1jEuLpxtFdtrK";

    public FutureWeather getWeatherData(String location, String durationInS) {
        String locationKey = getLocationKey(location);
        System.out.println("location is " + location);
        System.out.println("location key is + " + locationKey);
        HttpURLConnection con = null;
        InputStream is = null;
        try {
            String link = String.format(BASE_URL, locationKey, apiKey);
            System.out.println(link);
            con = (HttpURLConnection)(new URL(link).openConnection());
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.connect();

            // Let's read the response
            StringBuilder buffer = new StringBuilder();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) buffer.append(line).append("\r\n");
            is.close();
            con.disconnect();
            System.out.println(buffer.toString());
            JSONFutureWeatherParser jsonFutureWeatherParser = new JSONFutureWeatherParser();
            FutureWeather futureWeather = jsonFutureWeatherParser.getFutureWeather(buffer.toString(), durationInS);
            futureWeather.getLocation().setCity(location.split(", ")[0]);
            futureWeather.getLocation().setCountry(location.split(", ")[1]);
            futureWeather.getLocation().setLocationKey(locationKey);
            return futureWeather;
        } catch(Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch(Exception ignored) {}
            try {
                if (con != null) {
                    con.disconnect();
                }
            } catch(Throwable ignored) {}
        }
        return null;
    }

    private String getLocationKey(String location) {
        HttpURLConnection con = null;
        InputStream is = null;
        try {
            String link = String.format(GET_KEY_URL, location).replaceAll(" +", "%20");
            System.out.println(link);
            con = (HttpURLConnection)(new URL(link).openConnection());
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.connect();
            // Let's read the response
            StringBuilder buffer = new StringBuilder();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) buffer.append(line).append("\r\n");
            is.close();
            con.disconnect();
            System.out.println(buffer.toString());

            LocationParser locationParser = new LocationParser();
            System.out.println("got locationKey successfully");
            return locationParser.getLocationKey(buffer.toString());
        } catch(Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch(Exception ignored) {}
            try {
                if (con != null) {
                    con.disconnect();
                }
            } catch(Throwable ignored) {}
        }
        System.out.println("did not get locationKey");
        return "347630";
    }
}
