package cmu.cconfs.model.parseModel;

/**
 * @author jialingliu
 */
public class FutureWeather {

    public Location location = new Location();
    public String dateTime = "";
    public long epochDateTime = 0;

    public String iconPhrase = "";
    public boolean isDayLight = true;
    public Temperature temperature = new Temperature();

    public Probability probability = new Probability();

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

    public void setIconPhrase(String iconPhrase) {
        this.iconPhrase = iconPhrase;
    }

    public String getIconPhrase() {
        return this.iconPhrase;
    }
    public void setDayLight(boolean isDayLight) {
        this.isDayLight = isDayLight;
    }

    public boolean getDayLight() {
        return this.isDayLight;
    }

    public Temperature getTemperature() {
        return this.temperature;
    }

    public Wind getWind() {
        return this.wind;
    }

    public Probability getProbability() {
        return this.probability;
    }

    public Rain getRain() {
        return this.rain;
    }

    public Snow getSnow() {
        return this.snow;
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

    @Override
    public String toString() {
        return "[" + getLocation().getCity() + "\t" + getLocation().getCountry() + "\n" +
                getDateTime() + "\n" +
                getEpochDateTime() + "\n" +
                getIconPhrase() + "\n" +
                "isDayLight: " + this.getDayLight() + "\n" +
                getTemperature() + "\n" +
                getProbability() + "\n" +
                getWind() + "\n" +
                getRain() + "\n" +
                getSnow() + "\n" +
                getIce() + "\n" +
                "cloudCover: " + getCloudCover() + "\n" +
                "link: " + getLink() + "\n" +
                "mobileLink: " + getMobileLink() + "]";
    }

    public class Temperature {
        private double temp = 0;
        private double realFeelTemp = 0;
        private String unit = "";

        public Temperature() {

        }
        public Temperature(double temp, double realFeelTemp, String unit) {
            this.temp = temp;
            this.realFeelTemp = realFeelTemp;
            this.unit = unit;
        }
        public double getTemp() {
            return temp;
        }
        public void setTemp(double temp) {
            this.temp = temp;
        }
        public double getRealFeelTemp() {
            return realFeelTemp;
        }
        public void setRealFeelTemp(double realFeelTemp) {
            this.realFeelTemp = realFeelTemp;
        }
        public String getUnit() {
            return unit;
        }
        public void setUnit(String unit) {
            this.unit = unit;
        }

        @Override
        public String toString() {
            return "realFeelTemperature: " + this.getRealFeelTemp() + this.getUnit() + "\t" + ("temperature: ") + this.getTemp() + this.getUnit();
        }
    }

    public class Wind {
        // speed
        private double speed = 0;
        private String unit = "";

        // direction
        private double degree = 0;
        private String localized = "";

        public double getSpeed() {
            return speed;
        }
        public void setSpeed(double speed) {
            this.speed = speed;
        }
        public String getUnit() {
            return unit;
        }
        public void setUnit(String unit) {
            this.unit = unit;
        }

        public double getDegree() {
            return degree;
        }
        public void setDegree(double degree) {
            this.degree = degree;
        }
        public String getLocalized() {
            return localized;
        }
        public void setLocalized(String localized) {
            this.localized = localized;
        }

        @Override
        public String toString() {
            return getSpeed() + getUnit() + ", " + getDegree() + getLocalized();
        }
    }

    public class Probability {
        private double precipitationProbability = 0;
        private double rainProbability = 0;
        private double snowProbability = 0;
        private double iceProbability = 0;

        public void setPrecipitationProbability(double precipitationProbability) {
            this.precipitationProbability = precipitationProbability;
        }

        public double getPrecipitationProbability() {
            return this.precipitationProbability;
        }

        public void setRainProbability(double rainProbability) {
            this.rainProbability = rainProbability;
        }

        public double getRainProbability() {
            return this.rainProbability;
        }

        public void setSnowProbability(double snowProbability) {
            this.snowProbability = snowProbability;
        }

        public double getSnowProbability() {
            return this.snowProbability;
        }

        public void setIceProbability(double iceProbability) {
            this.iceProbability = iceProbability;
        }

        public double getIceProbability() {
            return this.iceProbability;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (getRainProbability() != 0) {
                sb.append(getRainProbability()).append("% ").append("rain");
            }
            if (getSnowProbability() != 0) {
                sb.append(getSnowProbability()).append("% ").append("snow");
            }
            if (getIceProbability() != 0) {
                sb.append(getIceProbability()).append("% ").append("ice");
            }
            if (sb.length() == 0) {
                sb.append("No probability of rain, snow or ice");
            }
            return sb.toString();
        }
    }

    public class Rain {
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

        @Override
        public String toString() {
            return getVal() + getUnit() + " rain";
        }
    }
    public class Snow {
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

        @Override
        public String toString() {
            return getVal() + getUnit() + " snow";
        }
    }
    public class Ice {
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

        @Override
        public String toString() {
            return getVal() + getUnit() + " ice";
        }
    }
}
