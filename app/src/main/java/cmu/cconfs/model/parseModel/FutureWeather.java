package cmu.cconfs.model.parseModel;

/**
 * @author jialingliu
 */
public class FutureWeather {

    public Location location = new Location();
    public String dateTime = "";
    public long epochDateTime = 0;

    public boolean isDayLight = true;
    public Temperature temperature = new Temperature();
    public Wind wind = new Wind();
    public Rain rain = new Rain();
    public Snow snow = new Snow();
    public Ice ice = new Ice();
    public int cloudCover = 0;
    public String link = "";
    public String mobileLink = "";

    public FutureWeather() {}

    // setters and getters
    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateTime() {
        return this.dateTime;
    }

    public void setEpochDateTime(long epochDateTime) {
        this.epochDateTime = epochDateTime;
    }

    public long getEpochDateTime() {
        return this.epochDateTime;
    }

    public void setDayLight(boolean isDayLight) {
        this.isDayLight = isDayLight;
    }

    public boolean getDayLight() {
        return this.isDayLight;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public Temperature getTemperature() {
        return this.temperature;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Wind getWind() {
        return this.wind;
    }

    public void setRain(Rain rain) {
        this.rain = rain;
    }

    public Rain getRain() {
        return this.rain;
    }

    public void setSnow(Snow snow) {
        this.snow = snow;
    }

    public Snow getSnow() {
        return this.snow;
    }

    public void setIce(Ice ice) {
        this.ice = ice;
    }

    public Ice getIce() {
        return this.ice;
    }

    public void setCloudCover(int cloudCover) {
        this.cloudCover = cloudCover;
    }

    public int getCloudCover() {
        return this.cloudCover;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return this.link;
    }

    public void setMobileLink(String mobileLink) {
        this.mobileLink = mobileLink;
    }

    public String getMobileLink() {
        return this.mobileLink;
    }

    public  class Temperature {
        private float temp = 0;
        private float realFeelTemp = 0;
        private String unit = "";

        public float getTemp() {
            return temp;
        }
        public void setTemp(float temp) {
            this.temp = temp;
        }
        public float getRealFeelTemp() {
            return realFeelTemp;
        }
        public void setRealFeelTemp(float realFeelTemp) {
            this.realFeelTemp = realFeelTemp;
        }
        public String getUnit() {
            return unit;
        }
        public void setUnit(String unit) {
            this.unit = unit;
        }
    }

    public  class Wind {
        // speed
        private float speed = 0;
        private String unit = "";

        // direction
        private float degree = 0;
        private String localized = "";

        public float getSpeed() {
            return speed;
        }
        public void setSpeed(float speed) {
            this.speed = speed;
        }
        public String getUnit() {
            return unit;
        }
        public void setUnit(String unit) {
            this.unit = unit;
        }

        public float getDegree() {
            return degree;
        }
        public void setDegree(float degree) {
            this.degree = degree;
        }
        public String getLocalized() {
            return localized;
        }
        public void setLocalized(String localized) {
            this.localized = localized;
        }
    }

    public  class Rain {
        private int val = 0;
        private String unit = "";

        public int getVal() {
            return val;
        }
        public void setVal(int val) {
            this.val = val;
        }
        public String getUnit() {
            return unit;
        }
        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
    public  class Snow {
        private int val = 0;
        private String unit = "";

        public int getVal() {
            return val;
        }
        public void setVal(int val) {
            this.val = val;
        }
        public String getUnit() {
            return unit;
        }
        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
    public  class Ice {
        private int val = 0;
        private String unit = "";

        public int getVal() {
            return val;
        }
        public void setVal(int val) {
            this.val = val;
        }
        public String getUnit() {
            return unit;
        }
        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
}
