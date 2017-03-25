package cmu.cconfs.model.parseModel;

/**
 * @author jialingliu
 */
public class FutureWeather {

    public Location location = new Location();
    public String DateTime = "";
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

    public  class Temperature {
        private float temp;
        private float realFeelTemp;
        private String unit;

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
        private float speed;
        private String unit;

        // direction
        private float degree;
        private String localized;

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
        private int val;
        private String unit;

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
        private int val;
        private String unit;

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
        private int val;
        private String unit;

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
