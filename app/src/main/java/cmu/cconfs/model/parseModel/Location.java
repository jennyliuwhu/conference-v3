package cmu.cconfs.model.parseModel;

import java.io.Serializable;
/**
 * @author jialingliu
 */
public class Location implements Serializable {

    private float longitude;
    private float latitude;
    private String country;
    private String city;
    private String locationKey;

    public float getLongitude() {
        return longitude;
    }
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
    public float getLatitude() {
        return latitude;
    }
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setLocationKey(String locationKey) {
        this.locationKey = locationKey;
    }
    public String getLocationKey() {
        return this.locationKey;
    }
    @Override
    public String toString() {
        return getCity() + ", " + getCountry() + getLocationKey();
    }
}