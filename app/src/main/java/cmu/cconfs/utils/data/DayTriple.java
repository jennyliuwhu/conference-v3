package cmu.cconfs.utils.data;

/**
 * Created by qiuzhexin on 1/3/17.
 */

public class DayTriple {
    public int year;
    public int month;
    public int dayInMonth;

    public DayTriple(int year, int month, int dayInMonth) {
        this.year = year;
        this.month = month;
        this.dayInMonth = dayInMonth;
    }

    @Override
    public String toString() {
        return "DayTriple{" +
                "year=" + year +
                ", month=" + month +
                ", dayInMonth=" + dayInMonth +
                '}';
    }
}
